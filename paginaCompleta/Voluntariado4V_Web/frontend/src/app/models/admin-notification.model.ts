export type NotificationType = 'ORG_REGISTER' | 'ORG_ACTIVITY' | 'VOL_REGISTER' | 'VOL_JOIN_ACTIVITY' 
  | 'JOIN_REQUEST_ACCEPTED' | 'JOIN_REQUEST_DENIED' | 'ACTIVITY_REQUEST_ACCEPTED' | 'ACTIVITY_REQUEST_DENIED';

export interface AdminNotification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  actionUrl: string; // The URL to navigate to when clicked
  // Optional metadata for display
  entityName?: string; // e.g., "Voluntarios Madrid" or "Juan Perez"
  targetName?: string; // e.g., "Limpieza de Playa" (for activity related)
  recipientId?: number; // ID of the user (Volunteer/Org) who should see this. Null/Undefined = Admin
  recipientRole?: 'admin' | 'organization' | 'volunteer'; // Role targeting
}
