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

    forkJoin({
      all: this.apiService.getActivities(),
      mine: this.apiService.getVolunteerActivities(this.userId)
    }).subscribe({
      next: (results) => {
        // Process my activities to a Set of IDs for O(1) lookup
        this.myActivityIds = new Set(results.mine.map((a: any) => a.id));

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
        this.message = '¡Te has inscrito correctamente!';
        setTimeout(() => this.message = '', 3000);
        this.loadActivities(); // Reload to update state
      },
      error: (err) => {
        this.message = 'Error al inscribirse: ' + (err.error?.error || 'Inténtalo de nuevo');
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
}
