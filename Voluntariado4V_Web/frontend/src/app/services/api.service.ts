import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private http = inject(HttpClient);
  public baseUrl = 'http://localhost:8000';
  private apiUrl = `${this.baseUrl}/api`;

  constructor() { }

  getUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuarios`);
  }

  getVolunteers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/volunteers`);
  }

  deleteVolunteer(id: number | string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/volunteers/${id}`);
  }

  updateVolunteerStatus(id: number | string, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/volunteers/${id}/status`, { status });
  }

  getVolunteer(id: number | string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/volunteers/${id}`);
  }

  getOrganizations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/organizations`);
  }

  getOrganization(id: number | string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/organizations/${id}`);
  }

  getAdmin(id: number | string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin/${id}`);
  }

  updateAdmin(id: number | string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/admin/${id}`, data);
  }

  updateOrganizationStatus(id: number | string, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/organizations/${id}/status`, { status });
  }

  getOrganizationActivities(orgId: number | string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/organizations/${orgId}/activities`);
  }

  getActivities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/activities`);
  }

  createActivity(activity: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/activities`, activity);
  }

  updateActivityStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/activities/${id}/status`, { status });
  }

  deleteActivity(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/activities/${id}`);
  }

  updateActivity(id: number, activity: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/activities/${id}`, activity);
  }

  // Auth Methods
  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials);
  }

  registerVolunteer(volunteer: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/volunteers`, volunteer);
  }

  registerOrganization(org: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/organizations`, org);
  }

  signUpForActivity(activityId: number, volunteerId: number, role?: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/activities/${activityId}/signup`, { volunteerId, role });
  }

  unsubscribeFromActivity(activityId: number, volunteerId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/activities/${activityId}/volunteers/${volunteerId}`);
  }

  getActivityVolunteers(activityId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/activities/${activityId}/volunteers`);
  }

  updateVolunteer(id: number | string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/volunteers/${id}`, data);
  }

  updateOrganization(id: number | string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/organizations/${id}`, data);
  }
  getVolunteerActivities(volunteerId: number | string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/volunteers/${volunteerId}/activities`);
  }

  getVolunteerRequests(id: number | string): Observable<any> {
    return this.http.get(`${this.apiUrl}/volunteers/${id}/requests`);
  }

  getOrganizationRequests(orgId: number | string, status?: string): Observable<any[]> {
    let url = `${this.apiUrl}/requests?organizationId=${orgId}`;
    if (status) {
      url += `&status=${status}`;
    }
    return this.http.get<any[]>(url);
  }

  updateRequestStatus(requestId: number, status: string): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/requests/${requestId}/status`, { status });
  }

  getCiclos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ciclos`);
  }

  uploadVolunteerAvatar(id: string | number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('avatar', file);
    return this.http.post(`${this.apiUrl}/volunteers/${id}/avatar`, formData);
  }

  uploadOrganizationAvatar(id: string | number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('avatar', file);
    return this.http.post(`${this.apiUrl}/organizations/${id}/avatar`, formData);
  }

  uploadAdminAvatar(id: string | number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('avatar', file);
    return this.http.post(`${this.apiUrl}/admin/${id}/avatar`, formData);
  }

  getActivityTypes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/activity-types`);
  }

  uploadActivityImage(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);
    return this.http.post(`${this.apiUrl}/activities/${id}/image`, formData);
  }

  getOds(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ods`);
  }
}
