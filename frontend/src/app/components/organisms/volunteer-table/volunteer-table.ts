import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../../atoms/avatar/avatar';
import { BadgeComponent } from '../../atoms/badge/badge';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

interface Volunteer {
  id: number;
  name: string;
  firstName: string;
  lastName: string;
  project: string;
  email: string;
  phone: string;
  lastActivity: string;
  status: 'active' | 'pending' | 'inactive' | 'org-pending' | 'suspended' | 'custom';
  avatar: string;
  dni: string;
  address: string;
  course: string;
  dateOfBirth: string;
  description: string;
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

  // Sorting
  sortColumn: string = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Search filter
  searchTerm: string = '';

  // Action dropdown
  activeDropdownId: number | null = null;

  // Modal control
  showDetailsModal: boolean = false;

  volunteers: Volunteer[] = [];

  ngOnInit() {
    this.loadVolunteers();
  }

  loadVolunteers() {
    this.apiService.getVolunteers().subscribe({
      next: (data) => {
        console.log('Volunteers received:', data);
        this.volunteers = data.map((v: any) => {
          const firstName = v.name || '';
          const lastName = `${v.surname1 || ''} ${v.surname2 || ''}`.trim();
          return {
            id: v.id,
            name: `${firstName} ${lastName}`.trim(),
            firstName: firstName,
            lastName: lastName,
            project: v.course || 'Sin Asignar',
            email: v.email,
            phone: v.phone,
            lastActivity: 'Reciente',
            status: this.mapStatus(v.status),
            avatar: 'assets/images/volunteer-avatar.png',
            dni: v.dni,
            address: 'No disponible',
            course: v.course,
            dateOfBirth: v.dateOfBirth || 'No disponible',
            description: v.description || 'Sin descripción',
            availability: [],
            interests: []
          };
        });
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
    let result = this.volunteers;

    // Filter by tab
    if (this.activeTab === 'requests') {
      result = result.filter(v => v.status === 'pending');
    } else {
      // Show both active AND suspended volunteers in registered tab
      result = result.filter(v => v.status === 'active' || v.status === 'suspended');
    }

    // Filter by search term
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(v =>
        v.name.toLowerCase().includes(term) ||
        v.email.toLowerCase().includes(term) ||
        v.phone.includes(term) ||
        v.dni.toLowerCase().includes(term)
      );
    }

    // Sort
    result = [...result].sort((a, b) => {
      let valA = (a as any)[this.sortColumn] || '';
      let valB = (b as any)[this.sortColumn] || '';

      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();

      if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    return result;
  }

  sort(column: string) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
  }

  toggleDropdown(volunteerId: number, event: Event) {
    event.stopPropagation();
    this.activeDropdownId = this.activeDropdownId === volunteerId ? null : volunteerId;
  }

  closeDropdown() {
    this.activeDropdownId = null;
  }

  suspendVolunteer(volunteer: Volunteer) {
    this.apiService.updateVolunteerStatus(volunteer.id, 'SUSPENDIDO').subscribe({
      next: () => {
        volunteer.status = 'suspended';
        this.closeDropdown();
      },
      error: (err) => {
        console.error('Error suspending volunteer', err);
        this.errorMessage = 'Error al suspender voluntario: ' + err.message;
      }
    });
  }

  setTab(tab: 'requests' | 'registered') {
    this.activeTab = tab;
  }

  openDetails(volunteer: Volunteer) {
    this.selectedVolunteer = volunteer;
    this.showDetailsModal = true;
    this.closeDropdown();
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedVolunteer = null;
  }

  openDeactivateConfirm(volunteer: Volunteer) {
    this.volunteerToDeactivate = volunteer;
  }

  deactivateVolunteer() {
    if (this.volunteerToDeactivate) {
      this.apiService.updateVolunteerStatus(this.volunteerToDeactivate.id, 'SUSPENDIDO').subscribe({
        next: () => {
          this.volunteerToDeactivate!.status = 'suspended';
          // Move out of registered list if we only show active ones there
          // this.volunteers = this.volunteers.filter(v => v.id !== this.volunteerToDeactivate!.id); 
          // However, if we want to show suspended in registered list but marked, we keep it. 
          // The request says "si das a denegar que pasen a estado 'suspended' y tambien dejen de aparecer" 
          // This usually applies to requests.
          // For registered users being deactivated, usually they should also disappear or go to a 'suspended' tab.
          // Based on "Registered" tab potentially showing active only, let's update local state so filter catches it.
          // The filtering logic `v.status === 'active'` will hide it automatically.
          this.volunteerToDeactivate = null;
        },
        error: (err) => {
          console.error('Error suspending volunteer', err);
          this.errorMessage = 'Error al suspender voluntario: ' + err.message;
        }
      });
    }
  }

  acceptVolunteer(volunteer: Volunteer) {
    this.apiService.updateVolunteerStatus(volunteer.id, 'ACTIVO').subscribe({
      next: () => {
        volunteer.status = 'active';
      },
      error: (err) => {
        console.error('Error accepting volunteer', err);
        this.errorMessage = 'Error al aceptar voluntario: ' + err.message;
      }
    });
  }

  denyVolunteer(volunteer: Volunteer) {
    if (confirm(`¿Estás seguro de que deseas denegar a ${volunteer.name}?`)) {
      this.apiService.updateVolunteerStatus(volunteer.id, 'SUSPENDIDO').subscribe({
        next: () => {
          volunteer.status = 'suspended';
        },
        error: (err) => {
          console.error('Error denying volunteer', err);
          this.errorMessage = 'Error al denegar voluntario: ' + err.message;
        }
      });
    }
  }
}
