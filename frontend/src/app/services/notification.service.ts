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

  notifyVolunteerJoinStatus(volunteerId: number, activityName: string, accepted: boolean) {
      const type = accepted ? 'JOIN_REQUEST_ACCEPTED' : 'JOIN_REQUEST_DENIED';
      const title = accepted ? 'Solicitud Aceptada' : 'Solicitud Denegada';
      const message = accepted 
          ? `Has sido aceptado en la actividad "${activityName}".`
          : `Tu solicitud para "${activityName}" ha sido denegada.`;
      
      this.addNotification({
          id: Date.now().toString(),
          type: type,
          title: title,
          message: message,
          timestamp: new Date(),
          read: false,
          actionUrl: '/volunteer-dashboard/activities', // Or my activities
          recipientRole: 'volunteer'
      });
  }

  notifyActivityRequestStatus(orgId: number, activityTitle: string, accepted: boolean) {
      const type = accepted ? 'ACTIVITY_REQUEST_ACCEPTED' : 'ACTIVITY_REQUEST_DENIED';
      const title = accepted ? 'Actividad Aceptada' : 'Actividad Denegada';
      const message = accepted 
          ? `La actividad "${activityTitle}" ha sido aprobada.`
          : `La actividad "${activityTitle}" ha sido rechazada.`;
      
      this.addNotification({
          id: Date.now().toString(),
          type: type,
          title: title,
          message: message,
          timestamp: new Date(),
          read: false,
          actionUrl: '/organization-dashboard/activities',
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

    console.log(`[NotificationService] Loading for Role: ${currentRole}, ID: ${currentId}`);

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
             return currentRole === 'organization' && (!n.recipientId || n.recipientId === currentId);
        }

        if (targetRole === 'volunteer') {
            // Must match role AND (if specific ID is set) match ID
            if (currentRole !== 'volunteer') return false;
            
            if (n.recipientId) {
                return n.recipientId === currentId;
            }
            return true; // Broadcast to all volunteers (e.g. "System down")
        }

        return false;
    });

    console.log(`[NotificationService] Filtered ${stored.length} -> ${filtered.length} notifications.`);
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
    // When saving, we must be careful not to overwrite "other people's" notifications if we only loaded "ours".
    // So we should probably load ALL from storage, merge OUR changes, and save back.
    // BUT for simplicity in this frontend-only mock:
    // We will just read ALL from storage again, find the ones we modified (by ID), update them, and save.
    
    const allStored = this.loadNotificationsFromStorage();
    const currentInMemory = notifications; // These are the ones we are working with (filtered)
    
    // Merge: update items in 'allStored' that exist in 'currentInMemory'
    const updatedAll = allStored.map(stored => {
        const found = currentInMemory.find(curr => curr.id === stored.id);
        return found ? found : stored;
    });
    
    // Also add any NEW ones that might be in currentInMemory but not in allStored (rare case if addNotification pushes to storage first)
    // Actually addNotification pushes to storage directly.
    
    localStorage.setItem('admin_notifications', JSON.stringify(updatedAll));
    
    // Re-emit filtered
    this.notificationsSubject.next(currentInMemory);
  }

  private getMockData(): AdminNotification[] {
    return [
      {
        id: '1',
        type: 'ORG_REGISTER',
        title: 'Nueva Organización',
        message: 'Fundación Ayuda Global está intentando registrarse.',
        entityName: 'Fundación Ayuda Global',
        timestamp: new Date(new Date().getTime() - 1000 * 60 * 10), 
        read: false,
        actionUrl: '/dashboard/organizations',
        recipientRole: 'admin'
      },
      // Mock for a volunteer (assuming ID 1 for testing)
      {
          id: '99',
          type: 'JOIN_REQUEST_ACCEPTED',
          title: 'Solicitud Aceptada',
          message: 'Has sido aceptado en "Comedor Social".',
          timestamp: new Date(new Date().getTime() - 1000 * 60 * 60),
          read: false,
          actionUrl: '/volunteer-dashboard/activities',
          recipientRole: 'volunteer',
          recipientId: 1 // We will assume the test user has ID 1
      }
    ];
  }
}
