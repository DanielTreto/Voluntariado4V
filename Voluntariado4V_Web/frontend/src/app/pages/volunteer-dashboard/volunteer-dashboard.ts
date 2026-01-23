import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar';
import { DashboardHeaderComponent } from '../../components/organisms/dashboard-header/dashboard-header';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-volunteer-dashboard',
  standalone: true,
  imports: [CommonModule, DashboardHeaderComponent, SidebarComponent, RouterOutlet],
  templateUrl: './volunteer-dashboard.html',
  styleUrls: ['./volunteer-dashboard.scss']
})
export class VolunteerDashboardComponent {
  links = [
    { label: 'Actividades', icon: 'bi-list-check', route: '/volunteer-dashboard/activities' },
    { label: 'Eventos', icon: 'bi-calendar-event-fill', route: '/volunteer-dashboard/events' },
    { label: 'Settings', icon: 'bi-gear-fill', route: '/volunteer-dashboard/settings' }
  ];
}
