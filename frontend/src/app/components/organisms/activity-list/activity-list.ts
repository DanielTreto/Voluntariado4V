import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../../atoms/avatar/avatar';
import { BadgeComponent } from '../../atoms/badge/badge';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';

interface Volunteer {
  id: number;
  name: string;
  avatar: string;
  email: string;
  phone: string;
  course: string;
  status: 'active' | 'pending' | 'suspended';
  interests: string[];
}

interface Organization {
  id: number;
  name: string;
  logo: string;
  email?: string;
  phone?: string;
}

interface Activity {
  id: number;
  title: string;
  description: string;
  location: string;
  date: string;
  image: string;
  organization: Organization;
  volunteers: Volunteer[];
  maxVolunteers?: number;
  type: 'Medio Ambiente' | 'Social' | 'Tecnológico' | 'Educativo' | 'Salud';
  status: 'active' | 'pending' | 'ended';
}

@Component({
  selector: 'app-activity-list',
  standalone: true,
  imports: [CommonModule, AvatarComponent, BadgeComponent, FormsModule],
  templateUrl: './activity-list.html',
  styleUrl: './activity-list.css'
})
export class ActivityListComponent implements OnInit {
  private apiService = inject(ApiService);
  private route = inject(ActivatedRoute);

  activeTab: 'pending' | 'active' | 'ended' = 'pending';

  allVolunteers: Volunteer[] = [];
  allOrganizations: Organization[] = [];
  activities: Activity[] = [];

  selectedActivity: Activity | null = null;
  activityToDelete: Activity | null = null;
  viewActivity: Activity | null = null;
  errorMessage: string = '';

  // Modal control
  showEditModal: boolean = false;
  showAddVolunteerModal: boolean = false;
  showDeleteModal: boolean = false;
  showViewModal: boolean = false;
  showCreateModal: boolean = false;

  // Volunteer filters
  volunteerFilterCourse: string = '';
  volunteerFilterStatus: string = '';
  volunteerSearchTerm: string = '';

  // For Create Activity
  newActivity: Partial<Activity> = {
    title: '',
    description: '',
    location: '',
    date: '',
    type: 'Social',
    image: 'assets/images/activity-1.jpg',
    organization: undefined,
    volunteers: [],
    status: 'pending'
  };
  selectedOrgId: number | null = null;

  ngOnInit() {
    this.loadData();
    this.loadVolunteers();
    this.loadOrganizations();
  }

  loadData() {
    combineLatest([
      this.apiService.getActivities(),
      this.route.queryParams
    ]).subscribe({
      next: ([data, params]) => {
        console.log('Activities received:', data);
        this.activities = data.map((act: any) => ({
          id: act.id,
          title: act.title,
          description: act.description,
          location: act.location || 'Ubicación no especificada',
          date: act.date,
          image: act.image || 'assets/images/activity-1.jpg',
          organization: act.organization || { id: 0, name: 'Sin organización', logo: 'assets/images/org-default.png' },
          volunteers: act.volunteers || [],
          maxVolunteers: act.maxVolunteers || 10,
          type: act.type || 'Social',
          status: this.mapStatus(act.status)
        }));

        const openId = params['openId'];
        if (openId) {
          const activityToOpen = this.activities.find(a => a.id == openId);
          if (activityToOpen) {
            console.log('Opening activity from link:', activityToOpen.title);
            this.activeTab = activityToOpen.status;

            // Open appropriate modal based on status
            if (activityToOpen.status === 'ended') {
              this.openViewDetails(activityToOpen);
            } else {
              this.openEdit(activityToOpen);
            }
          }
        }
      },
      error: (err) => {
        console.error('Error loading data', err);
        this.errorMessage = 'Error: ' + err.message;
      }
    });
  }

  loadVolunteers() {
    this.apiService.getVolunteers().subscribe({
      next: (data) => {
        this.allVolunteers = data.map((v: any) => ({
          id: v.id,
          name: `${v.name} ${v.surname1 || ''} ${v.surname2 || ''}`.trim(),
          avatar: 'assets/images/volunteer-avatar.png',
          email: v.email,
          phone: v.phone,
          course: v.course || 'Sin curso',
          status: this.mapVolunteerStatus(v.status),
          interests: v.interests || []
        }));
      },
      error: (err) => console.error('Error loading volunteers', err)
    });
  }

  loadOrganizations() {
    this.apiService.getOrganizations().subscribe({
      next: (data) => {
        this.allOrganizations = data
          .filter((org: any) => org.status === 'ACTIVO')
          .map((org: any) => ({
            id: org.id,
            name: org.name,
            logo: 'assets/images/org-default.png',
            email: org.email,
            phone: org.phone
          }));
      },
      error: (err) => console.error('Error loading organizations', err)
    });
  }

  mapStatus(backendStatus: string): 'active' | 'pending' | 'ended' {
    const map: any = {
      'PENDIENTE': 'pending',
      'EN_PROGRESO': 'active',
      'FINALIZADA': 'ended',
      'DENEGADA': 'ended'
    };
    return map[backendStatus] || 'pending';
  }

  mapVolunteerStatus(status: string): 'active' | 'pending' | 'suspended' {
    const map: any = {
      'PENDIENTE': 'pending',
      'ACTIVO': 'active',
      'SUSPENDIDO': 'suspended'
    };
    return map[status] || 'pending';
  }

  get filteredActivities() {
    return this.activities.filter(a => a.status === this.activeTab);
  }

  get filteredVolunteers() {
    let result = this.allVolunteers;

    if (this.volunteerSearchTerm.trim()) {
      const term = this.volunteerSearchTerm.toLowerCase();
      result = result.filter(v => v.name.toLowerCase().includes(term));
    }

    if (this.volunteerFilterCourse) {
      result = result.filter(v => v.course === this.volunteerFilterCourse);
    }

    if (this.volunteerFilterStatus) {
      result = result.filter(v => v.status === this.volunteerFilterStatus);
    }

    return result;
  }

  get uniqueCourses(): string[] {
    const courses = [...new Set(this.allVolunteers.map(v => v.course))];
    return courses.filter(c => c);
  }

  setTab(tab: 'pending' | 'active' | 'ended') {
    this.activeTab = tab;
  }

  // Edit Modal
  openEdit(activity: Activity) {
    this.selectedActivity = { ...activity, volunteers: [...activity.volunteers] };
    this.showEditModal = true;
  }

  closeEditModal() {
    this.showEditModal = false;
    this.selectedActivity = null;
  }

  saveActivity() {
    if (this.selectedActivity) {
      const payload = {
        title: this.selectedActivity.title,
        description: this.selectedActivity.description,
        location: this.selectedActivity.location,
        date: this.selectedActivity.date,
        type: this.selectedActivity.type
      };

      this.apiService.updateActivity(this.selectedActivity.id, payload).subscribe({
        next: () => {
          const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
          if (index !== -1) {
            this.activities[index] = { ...this.activities[index], ...payload };
          }
          this.closeEditModal();
        },
        error: (err) => {
          console.error('Error updating activity', err);
          // Fallback: update locally
          const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
          if (index !== -1) {
            this.activities[index] = this.selectedActivity!;
          }
          this.closeEditModal();
        }
      });
    }
  }

  // Add Volunteer Modal
  openAddVolunteer(activity: Activity) {
    this.selectedActivity = activity;
    this.volunteerFilterCourse = '';
    this.volunteerFilterStatus = '';
    this.volunteerSearchTerm = '';
    this.showAddVolunteerModal = true;
  }

  closeAddVolunteerModal() {
    this.showAddVolunteerModal = false;
    this.selectedActivity = null;
  }

  addVolunteerToActivity(volunteer: Volunteer) {
    if (this.selectedActivity) {
      if (!this.selectedActivity.volunteers.find(v => v.id === volunteer.id)) {
        this.selectedActivity.volunteers.push(volunteer);
        // Update the original activity in the array
        const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
        if (index !== -1) {
          this.activities[index].volunteers = [...this.selectedActivity.volunteers];
        }
      }
    }
  }

  removeVolunteerFromActivity(volunteer: Volunteer) {
    if (this.selectedActivity) {
      this.selectedActivity.volunteers = this.selectedActivity.volunteers.filter(v => v.id !== volunteer.id);
      const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
      if (index !== -1) {
        this.activities[index].volunteers = [...this.selectedActivity.volunteers];
      }
    }
  }

  isVolunteerInActivity(volunteer: Volunteer): boolean {
    return this.selectedActivity?.volunteers.some(v => v.id === volunteer.id) || false;
  }

  // Delete Modal
  openDeleteConfirm(activity: Activity) {
    this.activityToDelete = activity;
    this.showDeleteModal = true;
  }

  closeDeleteModal() {
    this.showDeleteModal = false;
    this.activityToDelete = null;
  }

  deleteActivity() {
    if (this.activityToDelete) {
      this.apiService.deleteActivity(this.activityToDelete.id).subscribe({
        next: () => {
          this.activities = this.activities.filter(a => a.id !== this.activityToDelete!.id);
          this.closeDeleteModal();
        },
        error: (err) => {
          console.error('Error deleting activity', err);
          // Fallback: remove locally
          this.activities = this.activities.filter(a => a.id !== this.activityToDelete!.id);
          this.closeDeleteModal();
        }
      });
    }
  }

  // View Modal (for Finalizadas)
  openViewDetails(activity: Activity) {
    this.viewActivity = activity;
    this.showViewModal = true;
  }

  closeViewModal() {
    this.showViewModal = false;
    this.viewActivity = null;
  }

  // Create Modal
  openCreateModal() {
    this.newActivity = {
      title: '',
      description: '',
      location: '',
      date: '',
      type: 'Social',
      image: 'assets/images/activity-1.jpg',
      organization: undefined,
      volunteers: [],
      status: 'pending'
    };
    this.selectedOrgId = null;
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  createActivity() {
    const org = this.allOrganizations.find(o => o.id === this.selectedOrgId);
    const payload = {
      title: this.newActivity.title,
      description: this.newActivity.description,
      location: this.newActivity.location,
      date: this.newActivity.date,
      type: this.newActivity.type,
      organizationId: this.selectedOrgId
    };

    this.apiService.createActivity(payload).subscribe({
      next: (response) => {
        console.log('Activity created', response);
        this.loadData();
        this.closeCreateModal();
      },
      error: (err) => {
        console.error('Error creating activity', err);
        this.errorMessage = 'Error al crear actividad: ' + err.message;
      }
    });
  }

  // Pending Actions
  acceptActivity(activity: Activity) {
    this.apiService.updateActivityStatus(activity.id, 'EN_PROGRESO').subscribe({
      next: () => {
        activity.status = 'active';
      },
      error: (err) => {
        console.error('Error accepting activity', err);
      }
    });
  }

  denyActivity(activity: Activity) {
    if (confirm(`¿Estás seguro de que deseas denegar esta actividad?`)) {
      this.apiService.updateActivityStatus(activity.id, 'DENEGADA').subscribe({
        next: () => {
          this.activities = this.activities.filter(a => a.id !== activity.id);
        },
        error: (err) => {
          console.error('Error denying activity', err);
        }
      });
    }
  }
}
