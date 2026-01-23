import { Component, Input, OnInit, inject, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { SearchBarComponent } from '../../molecules/search-bar/search-bar';
import { NotificationService } from '../../../services/notification.service';
import { AdminNotification } from '../../../models/admin-notification.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, SearchBarComponent, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent implements OnInit {
  @Input() title: string = 'Dashboard Admin';
  
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private elementRef = inject(ElementRef);
  
  notifications: AdminNotification[] = [];
  unreadCount: number = 0;
  showNotifications: boolean = false;

  ngOnInit() {
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
  
  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showNotifications = false;
    }
  }
}
