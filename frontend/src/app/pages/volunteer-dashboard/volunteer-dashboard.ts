import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/organisms/navbar/navbar';
import { SidebarComponent } from '../../components/organisms/sidebar/sidebar';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-volunteer-dashboard',
  standalone: true,
  imports: [CommonModule, NavbarComponent, SidebarComponent, RouterOutlet],
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
