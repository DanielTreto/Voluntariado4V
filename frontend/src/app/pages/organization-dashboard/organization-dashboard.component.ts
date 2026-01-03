import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';
import { DashboardHeaderComponent } from '../../components/organisms/dashboard-header/dashboard-header';
import { Footer } from '../../components/organisms/footer/footer';

@Component({
  selector: 'app-organization-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardHeaderComponent, Footer],
  templateUrl: './organization-dashboard.component.html',
  styleUrls: ['./organization-dashboard.component.scss']
})
export class OrganizationDashboardComponent implements OnInit {
  activities: any[] = [];
  selectedActivityVolunteers: any[] = [];
  selectedActivityId: number | null = null;
  userId: number | null = null;
  userRole: string | null = null;

  private apiService = inject(ApiService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.role === 'organization') {
      this.userId = user.id;
      this.userRole = user.role;
      this.loadActivities();
    } else {
      this.router.navigate(['/']); // Redirect if not organization
    }
  }

  loadActivities() {
    this.apiService.getActivities().subscribe({
      next: (data) => {
        console.log('Organization activities loaded:', data);
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        
        if (user && user.id) {
           const filtered = data.filter(a => a.organization?.id == user.id);
           this.activities = filtered;
           this.cdr.detectChanges();
        } else {
           console.warn('No user ID found for organization. User object:', user);
           this.activities = [];
        }
      },
      error: (err) => {
          console.error('Error loading activities', err);
      }
    });
  }

  addPlaceholders() {
      // Fake volunteer
      const vol = {
          name: 'Voluntario Ej (Placeholder)',
          email: 'vol@ejemplo.com',
          phone: '600123456'
      };

      this.activities = [
          {
              id: 999,
              title: 'Actividad de Prueba (Placeholder)',
              description: 'Esta es una actividad de prueba para visualizar el diseño.',
              date: '2025-01-01',
              status: 'PENDIENTE',
              volunteers: [vol] // Not actually used by viewVolunteers directly unless we mock API too, but good for structure
          },
          {
              id: 998,
              title: 'Taller de Cocina (Placeholder)',
              description: 'Aprende a cocinar saludable.',
              date: '2025-02-15',
              status: 'EN_PROGRESO',
              volunteers: []
          }
      ];
  }

  viewVolunteers(activityId: number) {
    this.selectedActivityId = activityId;
    
    // Check if it's a placeholder activity
    if (activityId >= 900) {
        const activity = this.activities.find(a => a.id === activityId);
        this.selectedActivityVolunteers = activity ? activity.volunteers : [];
        return;
    }

    this.apiService.getActivityVolunteers(activityId).subscribe({
      next: (data) => {
        this.selectedActivityVolunteers = data;
      },
      error: (err) => {
        console.error('Error loading volunteers', err);
        this.selectedActivityVolunteers = [];
      }
    });
  }

  closeVolunteers() {
    this.selectedActivityId = null;
    this.selectedActivityVolunteers = [];
  }
}
