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

  getOrganizations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/organizations`);
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

  getActivityVolunteers(activityId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/activities/${activityId}/volunteers`);
  }
}
