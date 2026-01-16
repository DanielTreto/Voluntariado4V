import { Component, Input, OnInit, inject, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotificationService } from '../../../services/notification.service';
import { AdminNotification } from '../../../models/admin-notification.model';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-header.html',
  styleUrls: ['./dashboard-header.scss']
})
export class DashboardHeaderComponent implements OnInit {
  @Input() title: string = 'Volunteer Management'; // Default title

  userName: string = 'Usuario';
  userRole: string = 'Voluntario';
  avatarSrc: string = 'assets/images/default-avatar.png'; // Default
  
  notifications: AdminNotification[] = [];
  unreadCount: number = 0;
  showNotifications: boolean = false;

  private router = inject(Router);
  private notificationService = inject(NotificationService);
  private elementRef = inject(ElementRef);

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.name) {
      this.userName = user.name;
      // Translate role for display
      this.userRole = user.role === 'organization' ? 'Organización' : 'Voluntario';
      // Set avatar (mock logic for now, or match Sidebar's previous asset)
      this.avatarSrc = user.avatar || 'assets/images/admin-avatar.png';
    }

    this.notificationService.notifications$.subscribe(notifs => {
      this.notifications = notifs;
      this.unreadCount = this.notificationService.getUnreadCount();
    });
  }

  toggleNotifications() {
    this.showNotifications = !this.showNotifications;
  }

  markAsRead(notification: AdminNotification) {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id);
    }
    this.showNotifications = false;
    this.router.navigateByUrl(notification.actionUrl);
  }

  markAllRead() {
    this.notificationService.markAllAsRead();
  }
  
  /* @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
     const target = event.target as HTMLElement;
     // Simple check to close if clicked outside, but carefully not to close when clicking the bell itself
     if (!this.elementRef.nativeElement.contains(target)) {
       this.showNotifications = false;
     }
  } */ 
  // Commented out HostListener to avoid conflict if bell click propagates to document immediately. 
  // Can be improved later or use a directive. simpler backdrop for now? 
  // Or just rely on elementRef check.
  
  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showNotifications = false;
    }
  }
  
  goHome() {
    this.router.navigate(['/']);
  }

  logout() {
    localStorage.removeItem('user');
    this.router.navigate(['/']);
  }
}
