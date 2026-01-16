import { Component } from '@angular/core';
import { DashboardHeaderComponent } from "../../components/organisms/dashboard-header/dashboard-header";
import { RouterOutlet } from "@angular/router";
import { SidebarComponent } from "../../components/organisms/sidebar/sidebar";

@Component({
  selector: 'app-dashboard',
  imports: [DashboardHeaderComponent, RouterOutlet, SidebarComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent {
  adminLinks = [
    { label: 'Dashboard', icon: 'bi-grid-fill', route: '/dashboard' },
    { label: 'Volunteers', icon: 'bi-people-fill', route: '/dashboard/volunteers' },
    { label: 'Organizations', icon: 'bi-building-fill', route: '/dashboard/organizations' },
    { label: 'Activities', icon: 'bi-list-check', route: '/dashboard/activities' },
    { label: 'Events', icon: 'bi-calendar-event-fill', route: '/dashboard/events' },
    { label: 'Reports', icon: 'bi-bar-chart-fill', route: '/dashboard/reports' },
    { label: 'Settings', icon: 'bi-gear-fill', route: '/dashboard/settings' }
  ];
}
