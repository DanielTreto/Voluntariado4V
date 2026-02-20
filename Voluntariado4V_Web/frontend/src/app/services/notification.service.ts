import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AdminNotification, NotificationType } from '../models/admin-notification.model';

const STORAGE_KEY = 'admin_notifications';
const STORAGE_VERSION_KEY = 'notif_version';
const CURRENT_VERSION = '3'; // Bump this to force-clear old incompatible data

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<AdminNotification[]>([]);
  notifications$ = this.notificationsSubject.asObservable();

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api';

  constructor() {
    // Clear stale localStorage data from previous incompatible versions
    if (localStorage.getItem(STORAGE_VERSION_KEY) !== CURRENT_VERSION) {
      localStorage.removeItem(STORAGE_KEY);
      localStorage.setItem(STORAGE_VERSION_KEY, CURRENT_VERSION);
    }
    this.loadNotifications();

    window.addEventListener('storage', () => this.loadNotifications());
  }

  /** Returns the current user from localStorage */
  private getCurrentUser(): { id: number; role: string } | null {
    const raw = localStorage.getItem('user');
    if (!raw) return null;
    const u = JSON.parse(raw);
    return { id: Number(u.id), role: u.role };
  }

  getUnreadCount(): number {
    return this.notificationsSubject.value.filter(n => !n.read).length;
  }

  markAsRead(id: string): void {
    const all = this.getAllFromStorage();
    const updated = all.map(n => n.id === id ? { ...n, read: true } : n);
    this.persistAll(updated);
  }

  markAllAsRead(): void {
    const user = this.getCurrentUser();
    if (!user) return;
    const all = this.getAllFromStorage().map(n =>
      n.recipientRole === user.role && Number(n.recipientId) === user.id
        ? { ...n, read: true }
        : n
    );
    this.persistAll(all);
  }

  addNotification(notification: AdminNotification) {
    // Reject notifications without a recipientRole
    if (!notification.recipientRole) {
      console.warn('[NotificationService] Skipped notification missing recipientRole', notification);
      return;
    }
    const all = this.getAllFromStorage();
    // Avoid duplicates by ID
    if (all.some(n => n.id === notification.id)) return;
    this.persistAll([notification, ...all]);
  }

  // -----------------------------------------------------------------------
  // Notification triggers
  // -----------------------------------------------------------------------

  /** Admin receives a notification when a new organization tries to register */
  notifyOrgRegistration(orgName: string) {
    this.addNotification({
      id: `org_reg_${Date.now()}`,
      type: 'ORG_REGISTER',
      title: 'Nueva Organización',
      message: `${orgName} está intentando registrarse.`,
      timestamp: new Date(),
      read: false,
      actionUrl: '/dashboard/organizations',
      recipientRole: 'admin'
      // No recipientId: all admins see this
    });
  }

  /** Admin receives a notification when a volunteer submits a join request */
  notifyAdminNewJoinRequest(volunteerName: string, activityTitle: string) {
    this.addNotification({
      id: `join_req_${Date.now()}`,
      type: 'VOL_JOIN_ACTIVITY',
      title: 'Nueva Solicitud de Voluntario',
      message: `${volunteerName} ha solicitado unirse a "${activityTitle}".`,
      timestamp: new Date(),
      read: false,
      actionUrl: '/dashboard/activities',
      recipientRole: 'admin'
      // No recipientId: all admins see this
    });
  }

  /** Volunteer receives a notification when their join request status changes */
  notifyVolunteerJoinStatus(volunteerId: number, activityName: string, accepted: boolean, activityId?: number) {
    const url = activityId
      ? `/volunteer-dashboard/activities?openId=${activityId}`
      : '/volunteer-dashboard/activities';
    this.addNotification({
      id: `req_status_${Date.now()}`,
      type: accepted ? 'JOIN_REQUEST_ACCEPTED' : 'JOIN_REQUEST_DENIED',
      title: accepted ? 'Solicitud Aceptada' : 'Solicitud Denegada',
      message: accepted
        ? `Has sido aceptado en la actividad "${activityName}".`
        : `Tu solicitud para "${activityName}" ha sido denegada.`,
      timestamp: new Date(),
      read: false,
      actionUrl: url,
      recipientRole: 'volunteer',
      recipientId: volunteerId
    });
  }

  /** Volunteer receives a notification when their account status changes */
  notifyVolunteerStatusUpdate(volunteerId: number | string, status: string) {
    const isAccepted = status === 'ACTIVO';
    this.addNotification({
      id: `vol_status_${Date.now()}`,
      type: 'VOL_REGISTER',
      title: isAccepted ? 'Cuenta Activada' : 'Cuenta Suspendida',
      message: isAccepted
        ? '¡Tu cuenta de voluntario ha sido activada! Ya puedes inscribirte en actividades.'
        : 'Tu cuenta ha sido suspendida temporalmente.',
      timestamp: new Date(),
      read: false,
      actionUrl: '/volunteer-dashboard',
      recipientRole: 'volunteer',
      recipientId: Number(volunteerId)
    });
  }

  /** Organization receives a notification when their activity is approved or denied */
  notifyActivityRequestStatus(orgId: number, activityTitle: string, accepted: boolean, activityId?: number) {
    const url = activityId
      ? `/organization-dashboard/activities?openId=${activityId}`
      : '/organization-dashboard/activities';
    this.addNotification({
      id: `act_status_${Date.now()}`,
      type: accepted ? 'ACTIVITY_REQUEST_ACCEPTED' : 'ACTIVITY_REQUEST_DENIED',
      title: accepted ? 'Actividad Aprobada' : 'Actividad Rechazada',
      message: accepted
        ? `La actividad "${activityTitle}" ha sido aprobada.`
        : `La actividad "${activityTitle}" ha sido rechazada.`,
      timestamp: new Date(),
      read: false,
      actionUrl: url,
      recipientRole: 'organization',
      recipientId: orgId
    });
  }

  /** Organization receives a notification when their account status changes */
  notifyOrganizationStatusUpdate(orgId: number | string, status: string) {
    const isAccepted = status === 'ACTIVO';
    this.addNotification({
      id: `org_status_${Date.now()}`,
      type: 'ORG_REGISTER',
      title: isAccepted ? 'Cuenta Activada' : 'Cuenta Suspendida',
      message: isAccepted
        ? '¡Tu organización ha sido aprobada! Ya puedes publicar actividades.'
        : 'Tu cuenta ha sido suspendida temporalmente por un administrador.',
      timestamp: new Date(),
      read: false,
      actionUrl: '/organization-dashboard',
      recipientRole: 'organization',
      recipientId: Number(orgId)
    });
  }

  /**
   * On admin startup: polls the API for pending join requests and creates
   * admin-specific notifications for ones not yet stored.
   */
  checkPendingRequestsForAdmin(): void {
    const user = this.getCurrentUser();
    if (!user || user.role !== 'admin') return;

    this.http.get<any[]>(`${this.apiUrl}/requests?status=PENDIENTE`).subscribe({
      next: (requests) => {
        if (!requests?.length) return;
        requests.forEach((req: any) => {
          const notifId = `join_req_${req.id}`;
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
            recipientRole: 'admin',
            recipientId: user.id
          });
        });
      },
      error: (err) => console.warn('[NotificationService] Could not fetch pending requests', err)
    });
  }

  /**
   * On volunteer startup: polls their own requests and notifies for any
   * accepted/denied ones not yet shown.
   */
  checkVolunteerRequestStatuses(volunteerId: number): void {
    this.http.get<any[]>(`${this.apiUrl}/volunteers/${volunteerId}/requests`).subscribe({
      next: (requests) => {
        if (!requests?.length) return;
        requests.forEach((req: any) => {
          if (req.status !== 'ACEPTADA' && req.status !== 'DENEGADA') return;
          const accepted = req.status === 'ACEPTADA';
          const notifId = `req_status_${req.id}_${req.status}`;
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
        });
      },
      error: (err) => console.warn('[NotificationService] Could not fetch volunteer requests', err)
    });
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  private loadNotifications(): void {
    const user = this.getCurrentUser();
    if (!user) {
      this.notificationsSubject.next([]);
      return;
    }

    const filtered = this.getAllFromStorage().filter(n => {
      if (n.recipientRole !== user.role) return false;

      // Admin notifications may target all admins (no recipientId) or a specific admin
      if (user.role === 'admin') {
        return !n.recipientId || Number(n.recipientId) === user.id;
      }

      // Volunteer and org notifications MUST match by recipientId
      return n.recipientId && Number(n.recipientId) === user.id;
    });

    this.notificationsSubject.next(filtered);
  }

  private getAllFromStorage(): AdminNotification[] {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  }

  private persistAll(notifications: AdminNotification[]): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notifications));
    this.loadNotifications();
  }
}
