import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../../atoms/avatar/avatar';
import { BadgeComponent } from '../../atoms/badge/badge';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

interface Volunteer {
  id: number;
  name: string;
  avatar: string;
  details?: string; // For "Ver más info"
}

interface Organization {
  name: string;
  logo: string;
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
  type: 'Medio Ambiente' | 'Social' | 'Tecnológico' | 'Educativo' | 'Salud';
  status: 'active' | 'pending' | 'ended';
}

@Component({
  selector: 'app-activity-list',
  standalone: true,
  imports: [CommonModule, AvatarComponent, FormsModule],
  templateUrl: './activity-list.html',
  styleUrl: './activity-list.css'
})
export class ActivityListComponent implements OnInit {
  private apiService = inject(ApiService);
  activeTab: 'active' | 'pending' | 'ended' = 'active';

  // Dummy data for all available volunteers - kept for UI demo
  allVolunteers: Volunteer[] = [
    { id: 1, name: 'Jane Doe', avatar: 'assets/images/volunteer-avatar.png', details: 'Experiencia en medio ambiente.' },
    { id: 2, name: 'John Smith', avatar: 'assets/images/volunteer-avatar.png', details: 'Experto en logística.' },
    { id: 3, name: 'Michael Brown', avatar: 'assets/images/volunteer-avatar.png', details: 'Ingeniero de software.' },
    { id: 4, name: 'Sarah Davis', avatar: 'assets/images/volunteer-avatar.png', details: 'Enfermera titulada.' },
    { id: 5, name: 'David Wilson', avatar: 'assets/images/volunteer-avatar.png', details: 'Profesor de secundaria.' }
  ];

  // Dummy data for organizations - kept for Create Form UI
  allOrganizations: Organization[] = [
    { name: 'EcoFriendly', logo: 'assets/images/org-logo-1.png' },
    { name: 'Tech4All', logo: 'assets/images/org-logo-2.png' },
    { name: 'Ayuda Solidaria', logo: 'assets/images/org-logo-1.png' },
    { name: 'Educa+', logo: 'assets/images/org-logo-2.png' }
  ];

  activities: Activity[] = [];
  selectedActivity: Activity | null = null;
  activityToDelete: Activity | null = null;
  errorMessage: string = '';

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
    status: 'active'
  };

  // For Add Volunteer
  selectedVolunteerId: number | null = null;
  selectedOrgName: string | null = null;

  ngOnInit() {
    this.loadActivities();
  }

  loadActivities() {
    this.apiService.getActivities().subscribe({
      next: (data) => {
        console.log('Activities received:', data);
        this.activities = data.map((act: any) => ({
          id: act.id,
          title: act.title,
          description: act.description,
          location: act.location,
          date: act.date,
          image: act.image,
          organization: act.organization || { name: 'Unknown', logo: 'assets/images/org-default.png' },
          volunteers: [], // Backend doesn't support volunteers relation yet
          type: act.type as any,
          status: this.mapStatus(act.status)
        }));
      },
      error: (err) => {
        console.error('Error loading activities', err);
        this.errorMessage = 'Error: ' + err.message;
      }
    });
  }

  mapStatus(backendStatus: string): 'active' | 'pending' | 'ended' {
    const map: any = {
      'PENDIENTE': 'pending',
      'EN_PROGRESO': 'active',
      'FINALIZADA': 'ended',
      'DENEGADA': 'denied' // Effectively hidden as not in tab types
    };
    return map[backendStatus] || 'pending';
  }

  get filteredActivities() {
    return this.activities.filter(a => a.status === this.activeTab);
  }

  setTab(tab: 'active' | 'pending' | 'ended') {
    this.activeTab = tab;
  }

  openEdit(activity: Activity) {
    this.selectedActivity = { ...activity };
  }

  openDeleteConfirm(activity: Activity) {
    this.activityToDelete = activity;
  }

  deleteActivity() {
    // Backend delete not requested/implemented for activities, mimicking deny behavior or just ui removal
    // Assuming delete = finish or deny? The prompt said "si rechazarlas 'denied'".
    // Standard delete implies removal. I'll just remove from UI for now unless DELETE endpoint added.
    if (this.activityToDelete) {
      this.activities = this.activities.filter(a => a.id !== this.activityToDelete!.id);
      this.activityToDelete = null;
    }
  }

  saveActivity() {
    // Edit functionality not explicitly detailed in prompt but keeping UI logic
    if (this.selectedActivity) {
      const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
      if (index !== -1) {
        this.activities[index] = this.selectedActivity;
      }
      this.selectedActivity = null;
    }
  }

  openAddVolunteer(activity: Activity) {
    this.selectedActivity = activity;
    this.selectedVolunteerId = null;
  }

  addVolunteer() {
    if (this.selectedActivity && this.selectedVolunteerId) {
       // Frontend only implementation as backend doesn't support it yet
      const volunteer = this.allVolunteers.find(v => v.id == this.selectedVolunteerId);
      if (volunteer) {
        if (!this.selectedActivity.volunteers.find(v => v.id === volunteer.id)) {
          this.selectedActivity.volunteers.push(volunteer);
        }
      }
      this.selectedVolunteerId = null;
      this.selectedActivity = null;
    }
  }

  viewVolunteerInfo() {
    if (this.selectedVolunteerId) {
      const volunteer = this.allVolunteers.find(v => v.id == this.selectedVolunteerId);
      if (volunteer) {
        alert(`Información del voluntario:\nNombre: ${volunteer.name}\nDetalles: ${volunteer.details}`);
      }
    }
  }

  // Create Activity
  createActivity() {
    const payload = {
        title: this.newActivity.title,
        description: this.newActivity.description,
        location: this.newActivity.location,
        date: this.newActivity.date,
        type: this.newActivity.type,
        // organizationId: logic to select org ID would go here, omitting for now
    };

    this.apiService.createActivity(payload).subscribe({
        next: (response) => {
            console.log('Activity created', response);
            // Reload to get fresh data with correct status (Default Pending)
            this.loadActivities();
            
            // Reset form
            this.newActivity = {
                title: '',
                description: '',
                location: '',
                date: '',
                type: 'Social',
                image: 'assets/images/activity-1.jpg',
                organization: undefined,
                volunteers: [],
                status: 'active'
            };
            this.selectedOrgName = null;
        },
        error: (err) => {
            console.error('Error creating activity', err);
            alert('Error creating activity: ' + err.message);
        }
    });
  }

  // Pending Actions
  acceptActivity(activity: Activity) {
    this.apiService.updateActivityStatus(activity.id, 'EN_PROGRESO').subscribe({
        next: () => {
            activity.status = 'active'; // Move to Active tab
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
                // Remove from view or move to denied tab if exists. 
                // Filter removes it from current 'pending' filtered list
                activity.status = 'active'; // Hack/Bug in thought process? No, mapStatus doesn't return denied.
                // Actually need to update local model to something that filters out.
                // mapStatus doesn't handle 'denied' explicitly in returned type, so activity.status type needs 'denied'? 
                // The interface Activity.status is 'active' | 'pending' | 'ended'. I should add 'denied' to interface or just remove from array.
                this.activities = this.activities.filter(a => a.id !== activity.id);
            },
            error: (err) => {
                console.error('Error denying activity', err);
            }
        });
      }
  }
}
