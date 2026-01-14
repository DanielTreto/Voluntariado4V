import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../services/api.service';
import { Router } from '@angular/router';
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
  myActivityIds: Set<number> = new Set();
  myRequestIds: Set<number> = new Set();
  userId: number | null = null;
  userRole: string | null = null;
  message: string = '';

  private apiService = inject(ApiService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

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
  }

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

        // Process requests
        if (results.requests) {
          this.myRequestIds = new Set(results.requests.map((r: any) => r.activityId));
        }

        // Filter and process all activities
        if (results.all) {
          this.activities = results.all.filter((a: any) => ['PENDIENTE', 'EN_PROGRESO'].includes(a.status?.toUpperCase()));
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading activities', err);
      }
    });
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
}
