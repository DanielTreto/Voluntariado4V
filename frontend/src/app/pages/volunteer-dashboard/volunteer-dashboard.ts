import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';
import { DashboardHeaderComponent } from '../../components/organisms/dashboard-header/dashboard-header';
import { Footer } from '../../components/organisms/footer/footer';

@Component({
  selector: 'app-volunteer-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardHeaderComponent, Footer],
  templateUrl: './volunteer-dashboard.html',
  styleUrls: ['./volunteer-dashboard.scss']
})
export class VolunteerDashboardComponent implements OnInit {
  activities: any[] = [];
  userId: number | null = null;
  userRole: string | null = null;
  message: string = '';

  private apiService = inject(ApiService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    // Check auth (simplified for now, ideally check token/service)
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.role === 'volunteer') {
      this.userId = user.id;
      this.userRole = user.role;
      this.loadActivities();
    } else {
      // If not logged in as volunteer, redirect to home
      this.router.navigate(['/']);
    }
  }

  loadActivities() {
    this.apiService.getActivities().subscribe({
      next: (data) => {
        if (data) {
            console.log('Volunteer Dashboard - Raw Data:', data);
            // Filter: Show PENDIENTE (for signup) and EN_PROGRESO (active)
            this.activities = data.filter(a => ['PENDIENTE', 'EN_PROGRESO'].includes(a.status?.toUpperCase()));
            
            this.cdr.detectChanges(); // Force update in case change detection is stuck
            
            if (this.activities.length === 0) {
                 console.log('No active activities found.');
            }
        }
      },
      error: (err) => {
        console.error('Error loading activities', err);
      }
    });
  }

  addPlaceholders() {
      this.activities = [
          {
              id: 999,
              title: 'Actividad de Prueba (Placeholder)',
              description: 'Esta es una actividad de prueba para visualizar el diseño.',
              date: '2025-01-01',
              status: 'PENDIENTE',
              organization: { name: 'Organización Demo' }
          },
          {
              id: 998,
              title: 'Taller de Cocina (Placeholder)',
              description: 'Aprende a cocinar saludable.',
              date: '2025-02-15',
              status: 'EN_PROGRESO',
              organization: { name: 'Comedor Social' }
          }
      ];
  }

  signUp(activityId: number) {
    if (!this.userId) return;

    this.apiService.signUpForActivity(activityId, this.userId).subscribe({
      next: (res) => {
        this.message = '¡Te has inscrito correctamente!';
        setTimeout(() => this.message = '', 3000);
        this.loadActivities(); // Reload to see updates if we track specific volunteer status
      },
      error: (err) => {
        this.message = 'Error al inscribirse: ' + (err.error?.error || 'Inténtalo de nuevo');
        setTimeout(() => this.message = '', 3000);
      }
    });
  }
}
