import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api'; // Ajusta esto si tu backend corre en otro puerto

  constructor() { }

  getUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuarios`);
  }

  getVolunteers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/volunteers`);
  }

  deleteVolunteer(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/volunteers/${id}`);
  }

  updateVolunteerStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/volunteers/${id}/status`, { status });
  }

  getVolunteer(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/volunteers/${id}`);
  }

  getOrganizations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/organizations`);
  }

  getOrganization(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/organizations/${id}`);
  }

  getAdmin(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/admin/${id}`);
  }

  updateAdmin(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/admin/${id}`, data);
  }

  updateOrganizationStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/organizations/${id}/status`, { status });
  }

  getOrganizationActivities(orgId: number): Observable<any[]> {
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

  signUpForActivity(activityId: number, volunteerId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/activities/${activityId}/signup`, { volunteerId });
  }

  unsubscribeFromActivity(activityId: number, volunteerId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/activities/${activityId}/volunteers/${volunteerId}`);
  }

  getActivityVolunteers(activityId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/activities/${activityId}/volunteers`);
  }

  updateVolunteer(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/volunteers/${id}`, data);
  }

  updateOrganization(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/organizations/${id}`, data);
  }
  getVolunteerActivities(volunteerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/volunteers/${volunteerId}/activities`);
  }

  getVolunteerRequests(id: number | string): Observable<any> {
    return this.http.get(`${this.apiUrl}/volunteers/${id}/requests`);
  }
}
