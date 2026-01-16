import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AdminNotification, NotificationType } from '../models/admin-notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<AdminNotification[]>([]);
  notifications$ = this.notificationsSubject.asObservable();

  constructor() {
    this.loadNotifications();
    
    // Listen for storage changes to sync across tabs
    window.addEventListener('storage', (event) => {
      if (event.key === 'admin_notifications') {
        this.loadNotifications();
      }
    });
  }

  getNotifications(): Observable<AdminNotification[]> {
    return this.notifications$;
  }

  getUnreadCount(): number {
    return this.notificationsSubject.value.filter(n => !n.read).length;
  }

  markAsRead(id: string): void {
    const current = this.notificationsSubject.value;
    const updated = current.map(n => n.id === id ? { ...n, read: true } : n);
    this.saveNotifications(updated);
  }

  markAllAsRead(): void {
    const current = this.notificationsSubject.value;
    const updated = current.map(n => ({ ...n, read: true }));
    this.saveNotifications(updated);
  }

  // Called by other components to simulating a real event
  addNotification(notification: AdminNotification) {
    const current = this.loadNotificationsFromStorage();
    const updated = [notification, ...current];
    this.saveNotifications(updated);
  }
  
  // Helper to trigger types of notifications easily
  notifyOrgRegistration(orgName: string) {
    this.addNotification({
        id: Date.now().toString(),
        type: 'ORG_REGISTER',
        title: 'Nueva Organización',
        message: `${orgName} está intentando registrarse.`,
        entityName: orgName,
        timestamp: new Date(),
        read: false,
        actionUrl: '/dashboard/organizations'
    });
  }

  private loadNotifications() {
    const stored = this.loadNotificationsFromStorage();
    this.notificationsSubject.next(stored);
  }

  private loadNotificationsFromStorage(): AdminNotification[] {
      const data = localStorage.getItem('admin_notifications');
      return data ? JSON.parse(data) : this.getMockData();
  }

  private saveNotifications(notifications: AdminNotification[]) {
    localStorage.setItem('admin_notifications', JSON.stringify(notifications));
    this.notificationsSubject.next(notifications);
  }

  private getMockData(): AdminNotification[] {
    return [
      {
        id: '1',
        type: 'ORG_REGISTER',
        title: 'Nueva Organización',
        message: 'Fundación Ayuda Global está intentando registrarse.',
        entityName: 'Fundación Ayuda Global',
        timestamp: new Date(new Date().getTime() - 1000 * 60 * 10), // 10 mins ago
        read: false,
        actionUrl: '/dashboard/organizations'
      }
    ];
  }
}
