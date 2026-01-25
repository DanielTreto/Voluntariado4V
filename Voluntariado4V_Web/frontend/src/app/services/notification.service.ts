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
      actionUrl: '/dashboard/organizations',
      recipientRole: 'admin'
    });
  }

  notifyVolunteerJoinStatus(volunteerId: number, activityName: string, accepted: boolean, activityId?: number) {
    const type = accepted ? 'JOIN_REQUEST_ACCEPTED' : 'JOIN_REQUEST_DENIED';
    const title = accepted ? 'Solicitud Aceptada' : 'Solicitud Denegada';
    const message = accepted
      ? `Has sido aceptado en la actividad "${activityName}".`
      : `Tu solicitud para "${activityName}" ha sido denegada.`;

    const url = activityId ? `/volunteer-dashboard/activities?openId=${activityId}` : '/volunteer-dashboard/activities';

    this.addNotification({
      id: Date.now().toString(),
      type: type,
      title: title,
      message: message,
      timestamp: new Date(),
      read: false,
      actionUrl: url,
      recipientRole: 'volunteer'
    });
  }

  notifyActivityRequestStatus(orgId: number, activityTitle: string, accepted: boolean, activityId?: number) {
    const type = accepted ? 'ACTIVITY_REQUEST_ACCEPTED' : 'ACTIVITY_REQUEST_DENIED';
    const title = accepted ? 'Actividad Aceptada' : 'Actividad Denegada';
    const message = accepted
      ? `La actividad "${activityTitle}" ha sido aprobada.`
      : `La actividad "${activityTitle}" ha sido rechazada.`;

    const url = activityId ? `/organization-dashboard/activities?openId=${activityId}` : '/organization-dashboard/activities';

    this.addNotification({
      id: Date.now().toString(),
      type: type,
      title: title,
      message: message,
      timestamp: new Date(),
      read: false,
      actionUrl: url,
      recipientId: orgId,
      recipientRole: 'organization'
    });
  }

  private loadNotifications() {
    // Determine current user context
    const userJson = localStorage.getItem('user');
    const user = userJson ? JSON.parse(userJson) : null;
    const currentRole = user?.role || 'admin';
    const currentId = user?.id;



    const stored = this.loadNotificationsFromStorage();

    // Filter notifications for the current user
    const filtered = stored.filter(n => {
      // 1. Normalize recipientRole: If missing, it's an ADMIN notification (legacy/default)
      const targetRole = n.recipientRole || 'admin';

      // 2. Strict Role Check
      if (targetRole === 'admin') {
        return currentRole === 'admin';
      }

      if (targetRole === 'organization') {
        if (currentRole !== 'organization') return false;
        // If specific recipient ID is set, it MUST match.
        if (n.recipientId && n.recipientId != currentId) return false;
        return true;
      }

      if (targetRole === 'volunteer') {
        if (currentRole !== 'volunteer') return false;
        // If specific recipient ID is set, it MUST match.
        if (n.recipientId && n.recipientId != currentId) return false;
        return true;
      }

      return false;
    });


    this.notificationsSubject.next(filtered);
  }

  private loadNotificationsFromStorage(): AdminNotification[] {
    const data = localStorage.getItem('admin_notifications');
    // If no data, separate mock data for filtered loading? 
    // Actually if localStorage is empty we load default mock.
    // But default mock is for admin. Let's add some mock for volunteer if empty.
    let all = data ? JSON.parse(data) : this.getMockData();
    return all;
  }

  private saveNotifications(notifications: AdminNotification[]) {
    const allStored = this.loadNotificationsFromStorage();

    // Create a map of stored items for easy updating
    const storedMap = new Map(allStored.map(n => [n.id, n]));

    // Update with new state from 'notifications' (filtered view)
    notifications.forEach(n => {
      storedMap.set(n.id, n);
    });

    // Convert back to array
    const updatedAll = Array.from(storedMap.values());

    localStorage.setItem('admin_notifications', JSON.stringify(updatedAll));

    // Re-emit the current filtered view (which is 'notifications')
    // We must ensure the subject emits exactly what was passed or a re-filtered version.
    // Since 'notifications' might be just the filtered subset (e.g. from markAsRead),
    // we should trust it portrays the latest state of THAT subset.
    this.notificationsSubject.next(notifications);
  }

  private getMockData(): AdminNotification[] {
    return [];
  }
}
