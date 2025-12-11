import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../../atoms/avatar/avatar';
import { BadgeComponent } from '../../atoms/badge/badge';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

interface Volunteer {
  id: number;
  name: string;
  project: string;
  email: string;
  phone: string;
  lastActivity: string;
  status: 'active' | 'pending' | 'inactive' | 'org-pending' | 'suspended' | 'custom';
  avatar: string;
  dni: string;
  address: string;
  course: string;
  availability: string[];
  interests: string[];
}

@Component({
  selector: 'app-volunteer-table',
  standalone: true,
  imports: [CommonModule, AvatarComponent, BadgeComponent, FormsModule],
  templateUrl: './volunteer-table.html',
  styleUrl: './volunteer-table.css'
})
export class VolunteerTableComponent implements OnInit {
  private apiService = inject(ApiService);
  activeTab: 'requests' | 'registered' = 'requests';
  selectedVolunteer: Volunteer | null = null;
  volunteerToDeactivate: Volunteer | null = null;
  errorMessage: string = '';

  volunteers: Volunteer[] = [];

  ngOnInit() {
    this.loadVolunteers();
  }

  loadVolunteers() {
    this.apiService.getVolunteers().subscribe({
      next: (data) => {
        console.log('Volunteers received:', data);
        this.volunteers = data.map((v: any) => ({
          id: v.id,
          name: `${v.name} ${v.surname1 || ''} ${v.surname2 || ''}`.trim(),
          project: v.course || 'Sin Asignar',
          email: v.email,
          phone: v.phone,
          lastActivity: 'Reciente',
          status: this.mapStatus(v.status),
          avatar: 'assets/images/volunteer-avatar.png',
          dni: v.dni,
          address: 'No disponible', 
          course: v.course,
          availability: [], 
          interests: []
        }));
      },
      error: (err) => {
        console.error('Error loading volunteers', err);
        this.errorMessage = 'Error loading data: ' + err.message;
      }
    });
  }

  mapStatus(status: string): 'active' | 'pending' | 'inactive' | 'org-pending' | 'suspended' | 'custom' {
    const map: any = {
      'PENDIENTE': 'pending',
      'ACTIVO': 'active',
      'SUSPENDIDO': 'suspended'
    };
    return map[status] || 'pending';
  }

  get filteredVolunteers() {
    // TEMPORARY: Return all volunteers to debug connection
    return this.volunteers; 
    /*
    if (this.activeTab === 'requests') {
      return this.volunteers.filter(v => v.status === 'pending');
    }
    return this.volunteers.filter(v => v.status !== 'pending');
    */
  }

  setTab(tab: 'requests' | 'registered') {
    this.activeTab = tab;
  }

  openDetails(volunteer: Volunteer) {
    this.selectedVolunteer = volunteer;
  }

  openDeactivateConfirm(volunteer: Volunteer) {
    this.volunteerToDeactivate = volunteer;
  }

  deactivateVolunteer() {
    if (this.volunteerToDeactivate) {
      this.volunteerToDeactivate.status = 'suspended';
      this.volunteerToDeactivate = null;
    }
  }

  acceptVolunteer(volunteer: Volunteer) {
    volunteer.status = 'active';
  }

  denyVolunteer(volunteer: Volunteer) {
    this.volunteers = this.volunteers.filter(v => v.id !== volunteer.id);
  }
}
