import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
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
  private cdr = inject(ChangeDetectorRef);

  activeTab: 'pending' | 'requests' | 'active' | 'ended' = 'active';

  allVolunteers: Volunteer[] = [];
  allOrganizations: Organization[] = [];
  activities: Activity[] = [];
  requests: any[] = [];
  pendingRequestsCount: number = 0;

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
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  // Volunteer filters
  volunteerFilterCourse: string = '';

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
    this.loadRequests(); // Preload requests count
  }

  loadData() {
    combineLatest([
      this.apiService.getActivities(),
      this.route.queryParams
    ]).subscribe({
      next: ([data, params]) => {

        this.activities = data.map((act: any) => ({
          id: act.id,
          title: act.title,
          description: act.description,
          location: act.location || 'Ubicación no especificada',
          date: act.date,
          image: act.image ? (act.image.startsWith('/uploads') || act.image.startsWith('http') ? (act.image.startsWith('/uploads') ? this.apiService.baseUrl + act.image : act.image) : act.image) : 'assets/images/activity-1.jpg',
          organization: act.organization ? {
            id: act.organization.id,
            name: act.organization.name,
            logo: act.organization.avatar ? (act.organization.avatar.startsWith('/uploads') ? this.apiService.baseUrl + act.organization.avatar : act.organization.avatar) : 'assets/images/org-default.png',
            email: act.organization.email,
            phone: act.organization.phone
          } : { id: 0, name: 'Sin organización', logo: 'assets/images/org-default.png' },
          volunteers: (act.volunteers || []).map((v: any) => ({
            ...v,
            avatar: v.avatar ? (v.avatar.startsWith('/uploads') ? this.apiService.baseUrl + v.avatar : v.avatar) : 'assets/images/volunteer-avatar.png'
          })),
          maxVolunteers: act.maxVolunteers || 10,
          type: act.type || 'Social',
          status: this.mapStatus(act.status)
        }));
        this.cdr.detectChanges();

        const openId = params['openId'];
        if (openId) {
          const activityToOpen = this.activities.find(a => a.id == openId);
          if (activityToOpen) {
            // console.log('Opening activity from link:', activityToOpen.title);
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

  loadRequests() {
    // Pass '' as orgId to specific behavior for Admin or just filter from backend if customized
    // Assuming getOrganizationRequests with empty string returns ALL (based on Controller logic).
    // If controller needs orgId, we might need a specific 'getAllRequests' endpoint or use existing with null.
    // Based on my controller code: if ($organizationId) it filters. If not, it returns all.
    this.apiService.getOrganizationRequests('', 'PENDIENTE').subscribe({
      next: (data) => {
        this.requests = data.map((req: any) => ({
          ...req,
          volunteer: {
            ...req.volunteer,
            avatar: req.volunteer?.avatar ? (req.volunteer.avatar.startsWith('/uploads') ? this.apiService.baseUrl + req.volunteer.avatar : req.volunteer.avatar) : 'assets/images/volunteer-avatar.png'
          }
        }));
        this.pendingRequestsCount = data.filter((r: any) => r.status === 'PENDIENTE').length;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading requests', err)
    });
  }

  updateRequestStatus(req: any, status: string) {
    if (!confirm(`¿Estás seguro de que quieres ${status === 'ACEPTADA' ? 'aceptar' : 'rechazar'} esta solicitud?`)) return;

    this.apiService.updateRequestStatus(req.id, status).subscribe({
      next: (res) => {
        req.status = status;
        this.loadRequests(); // Refresh list
        this.loadData(); // Refresh activities data to show new volunteers
        alert(`Solicitud ${status === 'ACEPTADA' ? 'aceptada' : 'rechazada'} correctamente.`);
      },
      error: (err) => {
        console.error('Error updating request', err);
        alert('Error al actualizar la solicitud');
      }
    });
  }

  loadVolunteers() {
    this.apiService.getVolunteers().subscribe({
      next: (data) => {
        this.allVolunteers = data.map((v: any) => ({
          id: v.id,
          name: `${v.name} ${v.surname1 || ''} ${v.surname2 || ''}`.trim(),
          avatar: v.avatar ? (v.avatar.startsWith('/uploads') ? this.apiService.baseUrl + v.avatar : v.avatar) : 'assets/images/volunteer-avatar.png',
          email: v.email,
          phone: v.phone,
          course: v.course || 'Sin curso',
          status: this.mapVolunteerStatus(v.status),
          interests: v.interests || [],
          dni: v.dni,
          birthDate: v.dateOfBirth,
          description: v.description,
          disponibilidades: v.disponibilidades || []
        }));

        this.cdr.detectChanges();
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
        this.cdr.detectChanges();
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
    // Only filter activities if tab is one of the activity states
    if (this.activeTab === 'requests') return [];
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

    return result.filter(v => v.status === 'active');
  }

  get uniqueCourses(): string[] {
    const courses = [...new Set(this.allVolunteers.map(v => v.course))];
    return courses.filter(c => c);
  }

  setTab(tab: 'pending' | 'requests' | 'active' | 'ended') {
    this.activeTab = tab;
    this.closeRequestInfoModal(); // Ensure isolated modal is closed
    if (tab === 'requests') {
      this.loadRequests();
    }
  }

  // Edit Modal
  openEdit(activity: Activity) {
    this.selectedActivity = { ...activity, volunteers: [...activity.volunteers] };
    this.selectedFile = null;
    this.imagePreview = activity.image;
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
          if (this.selectedFile) {
            this.apiService.uploadActivityImage(this.selectedActivity!.id, this.selectedFile).subscribe({
              next: () => {
                this.finishUpdate();
              },
              error: (err) => {
                console.error('Error uploading image', err);
                // Still finish update as text data was saved
                this.finishUpdate();
              }
            });
          } else {
            this.finishUpdate();
          }
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

  finishUpdate() {
    // Reload needed to get new image URL or just rely on local update?
    // Reloading is safer for image URL
    this.loadData();
    this.closeEditModal();
  }

  // Add Volunteer Modal
  openAddVolunteer(activity: Activity) {
    this.selectedActivity = activity;
    this.volunteerFilterCourse = '';
    this.volunteerSearchTerm = '';
    this.showAddVolunteerModal = true;
  }

  closeAddVolunteerModal() {
    this.showAddVolunteerModal = false;
    this.selectedActivity = null;
  }

  addVolunteerToActivity(volunteer: Volunteer) {
    if (this.selectedActivity && this.selectedActivity.id) {
      if (!this.selectedActivity.volunteers.find(v => v.id == volunteer.id)) {
        this.apiService.signUpForActivity(this.selectedActivity.id, volunteer.id, 'admin').subscribe({
          next: () => {
            this.selectedActivity!.volunteers.push(volunteer);
            // Update the original activity in the array
            const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
            if (index !== -1) {
              this.activities[index].volunteers = [...this.selectedActivity!.volunteers];
            }
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error adding volunteer', err);
            const msg = err.error && err.error.error ? err.error.error : 'Error al añadir voluntario. Inténtalo de nuevo.';
            alert(msg);
          }
        });
      }
    }
  }

  removeVolunteerFromActivity(volunteer: Volunteer) {
    if (this.selectedActivity && this.selectedActivity.id) {
      this.apiService.unsubscribeFromActivity(this.selectedActivity.id, volunteer.id).subscribe({
        next: () => {
          this.selectedActivity!.volunteers = this.selectedActivity!.volunteers.filter(v => v.id != volunteer.id);
          const index = this.activities.findIndex(a => a.id === this.selectedActivity!.id);
          if (index !== -1) {
            this.activities[index].volunteers = [...this.selectedActivity!.volunteers];
          }
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error removing volunteer', err);
          alert('Error al eliminar voluntario. Inténtalo de nuevo.');
        }
      });
    }
  }

  isVolunteerInActivity(volunteer: Volunteer): boolean {
    // Use loose equality to handle string vs number IDs
    return this.selectedActivity?.volunteers.some(v => v.id == volunteer.id) || false;
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
          this.cdr.detectChanges();
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
    this.selectedFile = null;
    this.imagePreview = null;
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  createActivity() {
    const org = this.allOrganizations.find(o => o.id === this.selectedOrgId);
    const payload = {
      title: this.newActivity.title,
      description: this.newActivity.description,
      location: this.newActivity.location,
      date: this.newActivity.date,
      type: this.newActivity.type,
      image: null, // Image handled via upload
      organizationId: this.selectedOrgId,
      role: 'admin' // Bypass validation
    };

    this.apiService.createActivity(payload).subscribe({
      next: (response) => {
        if (this.selectedFile) {
          this.apiService.uploadActivityImage(response.id, this.selectedFile).subscribe({
            next: () => {
              alert('Actividad creada con éxito');
              this.loadData();
              this.closeCreateModal();
            },
            error: (err) => {
              console.error('Error uploading image', err);
              alert('Actividad creada pero falló la subida de imagen');
              this.loadData();
              this.closeCreateModal();
            }
          });
        } else {
          this.loadData();
          this.closeCreateModal();
        }
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
        this.cdr.detectChanges();
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
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error denying activity', err);
        }
      });
    }
  }

  // Request Volunteer Info Modal (Isolated)
  showRequestInfoModal: boolean = false;
  selectedRequestVolunteer: any = null;

  openRequestInfo(volunteer: any) {
    if (!volunteer) return;


    setTimeout(() => {
      // Use loose equality == to handle string vs number ID mismatches
      const fullDetails = this.allVolunteers.find(v => v.id == volunteer.id);
      this.selectedRequestVolunteer = fullDetails || volunteer;


      this.showRequestInfoModal = true;
      this.cdr.detectChanges(); // Force UI update
    }, 0);
  }

  closeRequestInfoModal() {
    this.showRequestInfoModal = false;
    this.selectedRequestVolunteer = null;
  }
}
