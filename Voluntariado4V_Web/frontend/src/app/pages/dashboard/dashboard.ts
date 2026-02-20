import { Component, OnInit, inject } from '@angular/core';
import { DashboardHeaderComponent } from "../../components/organisms/dashboard-header/dashboard-header";
import { RouterOutlet } from "@angular/router";
import { SidebarComponent } from "../../components/organisms/sidebar/sidebar";
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-dashboard',
  imports: [DashboardHeaderComponent, RouterOutlet, SidebarComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit {
  private notificationService = inject(NotificationService);

  adminLinks = [
    { label: 'Dashboard', icon: 'bi-grid-fill', route: '/dashboard' },
    { label: 'Voluntarios', icon: 'bi-people', route: './volunteers' },
    { label: 'Organizaciones', icon: 'bi-building', route: './organizations' },
    { label: 'Actividades', icon: 'bi-calendar-event', route: './activities' },
    { label: 'Eventos', icon: 'bi-calendar3', route: './events' },
    { label: 'Informes', icon: 'bi-bar-chart', route: './reports' },
    { label: 'Ajustes', icon: 'bi-gear', route: './settings' }
  ];

  ngOnInit(): void {
    // Automatically populate admin notifications for pending join requests from API
    this.notificationService.checkPendingRequestsForAdmin();
  }
}
