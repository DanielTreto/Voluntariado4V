import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { DashboardHeaderComponent } from '../../components/organisms/dashboard-header/dashboard-header';
import { Footer } from '../../components/organisms/footer/footer';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar';

@Component({
  selector: 'app-organization-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardHeaderComponent, SidebarComponent, RouterOutlet],
  templateUrl: './organization-dashboard.html',
  styleUrls: ['./organization-dashboard.scss']
})
export class OrganizationDashboardComponent implements OnInit {
  userId: number | null = null;
  userRole: string | null = null;

  orgLinks = [
    { label: 'Actividades', icon: 'bi-list-check', route: '/organization-dashboard/activities' },
    { label: 'Eventos', icon: 'bi-calendar-event-fill', route: '/organization-dashboard/events' },
    { label: 'Ajustes', icon: 'bi-gear-fill', route: '/organization-dashboard/settings' }
  ];

  private router = inject(Router);

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.role === 'organization') {
      this.userId = user.id;
      this.userRole = user.role;
    } else {
      this.router.navigate(['/']);
    }
  }
}
