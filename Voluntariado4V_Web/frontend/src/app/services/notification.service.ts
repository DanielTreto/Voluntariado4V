import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AdminNotification, NotificationType } from '../models/admin-notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<AdminNotification[]>([]);
  notifications$ = this.notificationsSubject.asObservable();

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api';

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

  // -----------------------------------------------------------------------
  // Helper methods to trigger specific notification types
  // -----------------------------------------------------------------------

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

  /** Notifies the ADMIN that a volunteer has submitted a new join request */
  notifyAdminNewJoinRequest(volunteerName: string, activityTitle: string) {
    this.addNotification({
      id: Date.now().toString(),
      type: 'VOL_JOIN_ACTIVITY',
      title: 'Nueva Solicitud de Voluntario',
      message: `${volunteerName} ha solicitado unirse a "${activityTitle}".`,
      timestamp: new Date(),
      read: false,
      actionUrl: '/dashboard/activities',
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
      recipientRole: 'volunteer',
      recipientId: volunteerId
    });
  }

  notifyVolunteerStatusUpdate(volunteerId: number | string, status: string) {
    const isAccepted = status === 'ACTIVO';
    const title = isAccepted ? 'Cuenta Activada' : (status === 'SUSPENDIDO' ? 'Cuenta Suspendida' : 'Cuenta Actualizada');

    let message = '';
    if (isAccepted) {
      message = '¡Felicidades! Tu cuenta de voluntario ha sido activada. Ya puedes inscribirte en todas las actividades disponibles.';
    } else if (status === 'SUSPENDIDO') {
      message = 'Tu cuenta de voluntario ha sido suspendida temporalmente. No podrás inscribirte en nuevas actividades por ahora.';
    } else {
      message = `El estado de tu cuenta de voluntario ha cambiado a: ${status.toLowerCase()}.`;
    }


    this.addNotification({
      id: Date.now().toString(),
      type: isAccepted ? 'VOL_REGISTER' : 'VOL_REGISTER', // Reusing type or add new one
      title: title,
      message: message,
      timestamp: new Date(),
      read: false,
      actionUrl: '/volunteer-dashboard',
      recipientRole: 'volunteer',
      recipientId: Number(volunteerId)
    });
  }

  notifyOrganizationStatusUpdate(orgId: number | string, status: string) {
    const isAccepted = status === 'ACTIVO';
    const title = isAccepted ? 'Cuenta Activada' : (status === 'SUSPENDIDO' ? 'Cuenta Suspendida' : 'Cuenta Actualizada');

    let message = '';
    if (isAccepted) {
      message = '¡Buenas noticias! Tu organización ha sido aprobada. Ya puedes empezar a publicar actividades.';
    } else if (status === 'SUSPENDIDO') {
      message = 'Tu cuenta ha sido suspendida temporalmente por un administrador. Si crees que es un error, contáctanos.';
    } else {
      message = `El estado de tu organización ha sido actualizado a: ${status.toLowerCase()}.`;
    }


    this.addNotification({
      id: Date.now().toString(),
      type: isAccepted ? 'ORG_REGISTER' : 'ORG_REGISTER',
      title: title,
      message: message,
      timestamp: new Date(),
      read: false,
      actionUrl: '/organization-dashboard',
      recipientRole: 'organization',
      recipientId: Number(orgId)
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

  /**
   * Checks the API for pending volunteer join requests and creates admin notifications
   * for any that don't already have a corresponding notification in storage.
   * Should be called when admin loads the dashboard.
   */
  checkPendingRequestsForAdmin(): void {
    const userJson = localStorage.getItem('user');
    const user = userJson ? JSON.parse(userJson) : null;
    if (!user || user.role !== 'admin') return;

    this.http.get<any[]>(`${this.apiUrl}/requests?status=PENDIENTE`).subscribe({
      next: (requests) => {
        if (!requests || requests.length === 0) return;

        const stored = this.loadNotificationsFromStorage();

        requests.forEach((req: any) => {
          // Create a stable ID for this request notification to avoid duplicates
          const notifId = `join_req_${req.id}`;
          const alreadyExists = stored.some(n => n.id === notifId);

          if (!alreadyExists) {
            const volunteerName = req.volunteer?.fullName || req.volunteer?.name || 'Un voluntario';
            const activityTitle = req.activity?.title || 'una actividad';
            this.addNotification({
              id: notifId,
              type: 'VOL_JOIN_ACTIVITY',
              title: 'Solicitud Pendiente',
              message: `${volunteerName} quiere unirse a "${activityTitle}".`,
              timestamp: new Date(req.createdAt || Date.now()),
              read: false,
              actionUrl: '/dashboard/activities',
              recipientRole: 'admin'
            });
          }
        });
      },
      error: (err) => console.warn('Could not fetch pending requests for notification check', err)
    });
  }

  /**
   * Checks volunteer's own requests from the API for status changes and creates
   * notifications for any accepted/denied requests not yet notified.
   * Should be called when a volunteer loads their dashboard.
   */
  checkVolunteerRequestStatuses(volunteerId: number): void {
    this.http.get<any[]>(`${this.apiUrl}/volunteers/${volunteerId}/requests`).subscribe({
      next: (requests) => {
        if (!requests || requests.length === 0) return;

        const stored = this.loadNotificationsFromStorage();

        requests.forEach((req: any) => {
          if (req.status === 'ACEPTADA' || req.status === 'DENEGADA') {
            const accepted = req.status === 'ACEPTADA';
            const notifId = `req_status_${req.id}_${req.status}`;
            const alreadyExists = stored.some(n => n.id === notifId);

            if (!alreadyExists) {
              const activityId = req.activityId || req.activity?.id;
              const activityTitle = req.activityTitle || req.activity?.title || 'una actividad';
              const url = activityId
                ? `/volunteer-dashboard/activities?openId=${activityId}`
                : '/volunteer-dashboard/activities';

              this.addNotification({
                id: notifId,
                type: accepted ? 'JOIN_REQUEST_ACCEPTED' : 'JOIN_REQUEST_DENIED',
                title: accepted ? 'Solicitud Aceptada' : 'Solicitud Denegada',
                message: accepted
                  ? `Has sido aceptado en la actividad "${activityTitle}".`
                  : `Tu solicitud para "${activityTitle}" ha sido denegada.`,
                timestamp: new Date(),
                read: false,
                actionUrl: url,
                recipientRole: 'volunteer',
                recipientId: volunteerId
              });
            }
          }
        });
      },
      error: (err) => console.warn('Could not fetch volunteer requests for notification check', err)
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

    // Reload and re-filter notifications to ensure consistent state
    this.loadNotifications();
  }

  private getMockData(): AdminNotification[] {
    return [];
  }
}
