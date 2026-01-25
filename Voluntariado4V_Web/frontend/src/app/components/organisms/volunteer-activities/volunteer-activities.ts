import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../services/api.service';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-volunteer-activities',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './volunteer-activities.html',
  styleUrls: ['./volunteer-activities.scss']
})
export class VolunteerActivitiesComponent implements OnInit {
  activities: any[] = [];
  myActivitiesList: any[] = [];
  availableActivitiesList: any[] = [];
  currentTab: string = 'mis-actividades';

  myActivityIds: Set<number> = new Set();
  myRequestIds: Set<number> = new Set();
  userId: number | null = null;
  userRole: string | null = null;
  message: string = '';

  private apiService = inject(ApiService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    // Check auth
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.role === 'volunteer') {
      this.userId = user.id;
      this.userRole = user.role;
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

    const observables: any = {
      all: this.apiService.getActivities()
    };

    if (this.userRole === 'volunteer') {
      observables.mine = this.apiService.getVolunteerActivities(this.userId);
      observables.requests = this.apiService.getVolunteerRequests(this.userId);
    }

    forkJoin(observables).subscribe({
      next: (results: any) => {
        // Process my activities to a Set of IDs for O(1) lookup
        if (results.mine) {
          this.myActivityIds = new Set(results.mine.map((a: any) => a.id));
        } else {
          this.myActivityIds = new Set();
        }

        // Filter and process all activities first
        if (results.all) {
          this.activities = results.all.filter((a: any) => a.status?.toUpperCase() === 'EN_PROGRESO');
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
        console.error('Error loading activities', err);
        this.message = 'Error al cargar las actividades.';
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

    this.apiService.signUpForActivity(activityId, this.userId).subscribe({
      next: (res) => {
        this.message = '¡Solicitud enviada correctamente! Espera a que el administrador la acepte.';
        setTimeout(() => this.message = '', 5000);
        this.loadActivities(); // Reload to update state
      },
      error: (err) => {
        this.message = 'Error al enviar solicitud: ' + (err.error?.error || 'Inténtalo de nuevo');
        setTimeout(() => this.message = '', 3000);
      }
    });
  }

  unsignUp(activityId: number) {
    if (!this.userId) return;

    if (!confirm('¿Seguro que quieres desapuntarte de esta actividad?')) return;

    this.apiService.unsubscribeFromActivity(activityId, this.userId).subscribe({
      next: (res) => {
        this.message = 'Te has desapuntado correctamente.';
        setTimeout(() => this.message = '', 3000);
        this.loadActivities(); // Reload to update state
      },
      error: (err) => {
        this.message = 'Error al desapuntarse: ' + (err.error?.error || 'Inténtalo de nuevo');
        setTimeout(() => this.message = '', 3000);
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
    event.target.src = 'https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png';
  }
}
