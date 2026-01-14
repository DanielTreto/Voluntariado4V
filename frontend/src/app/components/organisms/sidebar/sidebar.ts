import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarLinkComponent } from '../../molecules/sidebar-link/sidebar-link';
import { UserProfileComponent } from '../../molecules/user-profile/user-profile';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, SidebarLinkComponent, UserProfileComponent],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class SidebarComponent implements OnInit {
  links: any[] = [];
  userRole: string = 'admin';

  ngOnInit() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.userRole = user.role || 'admin';
    }

    if (this.userRole === 'volunteer') {
      this.links = [
        { label: 'Actividades', icon: 'bi-list-check', route: '/volunteer-dashboard/activities' },
        { label: 'Eventos', icon: 'bi-calendar-event-fill', route: '/volunteer-dashboard/events' },
        { label: 'Settings', icon: 'bi-gear-fill', route: '/volunteer-dashboard/settings' }
      ];
    } else {
      this.links = [
        { label: 'Dashboard', icon: 'bi-grid-fill', route: '/dashboard' },
        { label: 'Volunteers', icon: 'bi-people-fill', route: '/dashboard/volunteers' },
        { label: 'Organizations', icon: 'bi-building-fill', route: '/dashboard/organizations' },
        { label: 'Activities', icon: 'bi-list-check', route: '/dashboard/activities' },
        { label: 'Events', icon: 'bi-calendar-event-fill', route: '/dashboard/events' },
        { label: 'Reports', icon: 'bi-bar-chart-fill', route: '/dashboard/reports' },
        { label: 'Settings', icon: 'bi-gear-fill', route: '/dashboard/settings' }
      ];
    }
  }
}
