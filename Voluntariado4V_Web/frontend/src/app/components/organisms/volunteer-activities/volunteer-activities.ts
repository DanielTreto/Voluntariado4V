import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../atoms/loading-spinner/loading-spinner.component';
import { SkeletonComponent } from '../../atoms/skeleton/skeleton.component';
import { finalize } from 'rxjs/operators';
import { ApiService } from '../../../services/api.service';
import { ToastService } from '../../../services/toast.service';
import { NotificationService } from '../../../services/notification.service';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-volunteer-activities',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, SkeletonComponent],
  templateUrl: './volunteer-activities.html',
  styleUrls: ['./volunteer-activities.scss']
})
export class VolunteerActivitiesComponent implements OnInit {
  isLoading: boolean = true;
  activities: any[] = [];
  myActivitiesList: any[] = [];
  availableActivitiesList: any[] = [];
  currentTab: string = 'mis-actividades';

  myActivityIds: Set<number> = new Set();
  myRequestIds: Set<number> = new Set();
  userId: number | null = null;
  userRole: string | null = null;
  // message state removed in favor of ToastService

  private apiService = inject(ApiService);
  private toastService = inject(ToastService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    // Check auth
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.role === 'volunteer') {
      this.userId = user.id;
      this.userRole = user.role;
      // Check if any requests were accepted/denied since last visit
      this.notificationService.checkVolunteerRequestStatuses(user.id);
      this.loadActivities();
    } else {
      this.userId = user.id; // temporary fallback
      this.loadActivities();
    }

    // Check for deep link
    this.route.queryParams.subscribe(params => {
      const openId = params['openId'];
      if (openId) {
        // We need to wait for activities to load. 
        // If activities are not loaded yet, this might fail unless we check after load.
        // Moving this logic to loadActivities or using a flag?
        // Simplest: check in loadActivities after data arrives.
        this.pendingOpenId = +openId;
      }
    });
  }

  // Pending open ID to handle raciness between route and data load
  pendingOpenId: number | null = null;

  loadActivities() {
    if (!this.userId) return;

    this.isLoading = true;

    const observables: any = {
      all: this.apiService.getActivities()
    };

    if (this.userRole === 'volunteer') {
      observables.mine = this.apiService.getVolunteerActivities(this.userId);
      observables.requests = this.apiService.getVolunteerRequests(this.userId);
    }

    forkJoin(observables).pipe(
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (results: any) => {
        // Process my activities to a Set of IDs for O(1) lookup
        if (results.mine) {
          this.myActivityIds = new Set(results.mine.map((a: any) => a.id));
        } else {
          this.myActivityIds = new Set();
        }

        // Filter and process all activities first - only show approved (EN_PROGRESO, APROBADA) activities to volunteers
        if (results.all) {
          this.activities = results.all.filter((a: any) => ['EN_PROGRESO', 'APROBADA'].includes(a.status?.toUpperCase()));
        }

        // Process requests
        this.myRequestIds = new Set();
        if (results.requests) {
          results.requests.forEach((req: any) => {
            this.myRequestIds.add(req.activityId);
            // Optionally merge request status into the activity object if needed for display
            const activityInList = this.activities.find(a => a.id === req.activityId);
            if (activityInList) {
              activityInList.requestStatus = req.status; // e.g. 'PENDIENTE', 'DENEGADA'
            }
          });
        }

        this.filterActivities();
        this.cdr.detectChanges();

        if (this.pendingOpenId) {
          const act = this.activities.find(a => a.id === this.pendingOpenId);
          if (act) {
            this.openActivityDetails(act);
            this.pendingOpenId = null; // Clear it
          }
        }
      },
      error: (err) => {
        console.error('Error loading activities:', err);
        this.toastService.show('Error al cargar las actividades.', 'error');
      }
    });
  }

  filterActivities() {
    this.myActivitiesList = this.activities.filter(a => this.isSignedUp(a.id) || this.isRequested(a.id));
    this.availableActivitiesList = this.activities.filter(a => !this.isSignedUp(a.id) && !this.isRequested(a.id));
  }

  setTab(tab: string) {
    this.currentTab = tab;
  }

  signUp(activityId: number) {
    if (!this.userId) return;

    const activity = this.activities.find((a: any) => a.id === activityId);

    this.apiService.signUpForActivity(activityId, this.userId).subscribe({
      next: (res) => {
        // Notify admin about the new pending join request
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const volunteerName = user.name || 'Un voluntario';
        const activityTitle = activity?.title || 'una actividad';
        this.notificationService.notifyAdminNewJoinRequest(volunteerName, activityTitle);

        this.toastService.show('¡Solicitud enviada correctamente! Espera a que el administrador la acepte.', 'success');
        this.loadActivities(); // Reload to update state
      },
      error: (err) => {
        this.toastService.show('Error al enviar solicitud: ' + (err.error?.error || 'Inténtalo de nuevo'), 'error');
      }
    });
  }

  unsignUp(activityId: number) {
    if (!this.userId) return;

    if (!confirm('¿Seguro que quieres desapuntarte de esta actividad?')) return;

    this.apiService.unsubscribeFromActivity(activityId, this.userId).subscribe({
      next: (res) => {
        this.toastService.show('Te has desapuntado correctamente.', 'success');
        this.loadActivities(); // Reload to update state
      },
      error: (err) => {
        this.toastService.show('Error al desapuntarse: ' + (err.error?.error || 'Inténtalo de nuevo'), 'error');
      }
    });
  }

  isSignedUp(activityId: number): boolean {
    return this.myActivityIds.has(activityId);
  }

  isRequested(activityId: number): boolean {
    return this.myRequestIds.has(activityId);
  }

  // Activity Details Modal
  showDetailsModal: boolean = false;
  selectedActivity: any = null;

  openActivityDetails(activity: any) {
    this.selectedActivity = activity;
    this.showDetailsModal = true;
    this.cdr.detectChanges();
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedActivity = null;
  }

  handleImageError(event: any) {
    event.target.src = 'assets/images/activity-default.jpg';
  }

  parseDate(dateString: string): Date | null {
    if (!dateString) return null;

    // Pattern: dd/MM/yy
    const match = dateString.match(/^(\d{2})\/(\d{2})\/(\d{2})$/);
    if (match) {
      const day = parseInt(match[1], 10);
      const month = parseInt(match[2], 10) - 1; // Month is 0-indexed
      let year = parseInt(match[3], 10);
      // Assume 2000s for two digit years if it makes sense, e.g., 2026
      year += 2000;

      return new Date(year, month, day);
    }

    // Fallback if the date is already in standard ISO or other format passing Date constructor
    const d = new Date(dateString);
    if (!isNaN(d.getTime())) return d;

    return null;
  }
}
