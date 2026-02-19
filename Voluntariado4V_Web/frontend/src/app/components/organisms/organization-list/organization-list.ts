import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BadgeComponent } from '../../atoms/badge/badge';
import { ApiService } from '../../../services/api.service';
import { ToastService } from '../../../services/toast.service';
import { InputTextComponent } from '../../atoms/input-text/input-text.component';

import { LoadingSpinnerComponent } from '../../atoms/loading-spinner/loading-spinner.component';
import { SkeletonComponent } from '../../atoms/skeleton/skeleton.component';
import { finalize } from 'rxjs/operators';
import { NotificationService } from '../../../services/notification.service';


interface Activity {
  id: number;
  title: string;
  description: string;
  date: string;
  status: string;
}

interface Organization {
  id: number;
  name: string;
  email: string;
  date?: string;
  activitiesCount: number;
  activities: Activity[];
  status: 'active' | 'pending' | 'org-pending' | 'inactive' | 'suspended';
  logo: string;
  type?: string;
  phone?: string;
  sector?: string;
  scope?: string;
  description?: string;
}

@Component({
  selector: 'app-organization-list',
  standalone: true,
  imports: [CommonModule, BadgeComponent, FormsModule, ReactiveFormsModule, InputTextComponent, LoadingSpinnerComponent, SkeletonComponent],

  templateUrl: './organization-list.html',
  styleUrl: './organization-list.css'
})
export class OrganizationListComponent implements OnInit {
  private apiService = inject(ApiService);
  private cdr = inject(ChangeDetectorRef);
  private fb = inject(FormBuilder);
  private toastService = inject(ToastService);
  private notificationService = inject(NotificationService);

  activeTab: 'pending' | 'registered' = 'pending';
  isLoading: boolean = true;
  selectedOrg: Organization | null = null;

  orgToSuspend: Organization | null = null;
  errorMessage: string = '';

  // Sorting
  sortColumn: string = 'name';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Search filter
  searchTerm: string = '';

  // Dropdown and modal control
  activeDropdownId: number | null = null;
  showDetailsModal: boolean = false;
  showEditModal: boolean = false;
  showCreateModal: boolean = false;
  editForm: FormGroup;
  createForm: FormGroup;
  editingOrgId: number | null = null;

  loadingActivities: boolean = false;

  organizations: Organization[] = [];
  allActivities: Activity[] = [];

  constructor() {
    this.editForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      type: [''],
      sector: [''],
      scope: [''],
      contactPerson: [''],
      description: [''],
      address: [''],
      web: [''],
      status: ['ACTIVO']
    });

    this.createForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['123456', [Validators.required, Validators.minLength(6)]], // Default password
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
      type: ['ONG', Validators.required],
      sector: ['SOCIAL', Validators.required],
      scope: ['LOCAL', Validators.required],
      contactPerson: [''],
      description: [''],
      address: [''],
      web: ['']
    });

  }

  ngOnInit() {
    this.loadOrganizations();
    this.loadAllActivities();
  }

  loadOrganizations() {
    this.isLoading = true;
    this.apiService.getOrganizations()
      .pipe(
        finalize(() => {
          setTimeout(() => {
            this.isLoading = false;
            this.cdr.detectChanges();
          });
        })
      )
      .subscribe({
        next: (data) => {
          this.organizations = data.map((org: any) => ({
            id: org.id,
            name: org.name,
            email: org.email,
            date: 'N/A',
            activitiesCount: 0,
            activities: [],
            status: this.mapStatus(org.status),
            logo: 'assets/images/org-default.png',
            type: org.type,
            phone: org.phone,
            sector: org.sector,
            scope: org.scope,
            description: org.description
          }));
          // Update activities count after loading
          this.updateActivitiesCounts();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error loading organizations', err);
          this.toastService.show('Error al cargar organizaciones.', 'error');
        }
      });
  }



  loadAllActivities() {
    this.apiService.getActivities().subscribe({
      next: (data) => {
        this.allActivities = data.map((act: any) => ({
          id: act.id,
          title: act.title,
          description: act.description,
          date: act.startDate || act.date,
          status: act.status,
          organizationId: act.organization?.id

        }));
        this.updateActivitiesCounts();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading activities', err);
      }
    });
  }

  updateActivitiesCounts() {
    this.organizations.forEach(org => {
      const orgActivities = this.allActivities.filter((act: any) => act.organizationId === org.id);
      org.activitiesCount = orgActivities.length;
      org.activities = orgActivities;
    });
  }

  mapStatus(status: string): 'active' | 'pending' | 'org-pending' | 'inactive' | 'suspended' {
    const map: any = {
      'PENDIENTE': 'org-pending',
      'ACTIVO': 'active',
      'SUSPENDIDO': 'suspended'
    };
    return map[status] || 'pending';
  }

  get pendingOrgs(): Organization[] {
    let result = this.organizations.filter(o => o.status === 'org-pending' || o.status === 'pending');

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(o =>
        o.name.toLowerCase().includes(term) ||
        o.email.toLowerCase().includes(term)
      );
    }

    return this.sortOrgs(result);
  }

  get registeredOrgs(): Organization[] {
    // Only show active organizations (suspended are hidden)
    let result = this.organizations.filter(o => o.status === 'active');

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(o =>
        o.name.toLowerCase().includes(term) ||
        o.email.toLowerCase().includes(term)
      );
    }

    return this.sortOrgs(result);
  }

  sortOrgs(orgs: Organization[]): Organization[] {
    return [...orgs].sort((a, b) => {
      let valA = (a as any)[this.sortColumn] || '';
      let valB = (b as any)[this.sortColumn] || '';

      if (typeof valA === 'string') valA = valA.toLowerCase();
      if (typeof valB === 'string') valB = valB.toLowerCase();

      if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }

  sort(column: string) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
  }

  setActiveTab(tab: 'pending' | 'registered') {
    this.activeTab = tab;
  }

  toggleDropdown(orgId: number, event: Event) {
    event.stopPropagation();
    this.activeDropdownId = this.activeDropdownId === orgId ? null : orgId;
  }

  closeDropdown() {
    this.activeDropdownId = null;
  }

  openDetails(org: Organization) {
    this.selectedOrg = org;
    this.showDetailsModal = true;
    this.closeDropdown();
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedOrg = null;
  }

  acceptOrg(org: Organization) {
    this.apiService.updateOrganizationStatus(org.id, 'ACTIVO').subscribe({
      next: () => {
        org.status = 'active';
        this.toastService.show('Organización aceptada', 'success');
        this.notificationService.notifyOrganizationStatusUpdate(org.id, 'ACTIVO');
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error accepting organization', err);
        this.errorMessage = 'Error al aceptar organización: ' + err.message;
      }
    });
  }

  denyOrg(org: Organization) {
    if (confirm(`¿Estás seguro de que deseas denegar a ${org.name}?`)) {
      this.apiService.updateOrganizationStatus(org.id, 'SUSPENDIDO').subscribe({
        next: () => {
          org.status = 'suspended';
          this.toastService.show('Organización denegada', 'info');
          this.notificationService.notifyOrganizationStatusUpdate(org.id, 'SUSPENDIDO');
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error denying organization', err);
          this.errorMessage = 'Error al denegar organización: ' + err.message;
        }
      });
    }
  }

  openSuspendConfirm(org: Organization) {
    this.orgToSuspend = org;
  }

  // Dar de baja - changes status to suspended (hidden from frontend but kept in DB)
  darDeBaja(org: Organization) {
    this.apiService.updateOrganizationStatus(org.id, 'SUSPENDIDO').subscribe({
      next: () => {
        org.status = 'suspended';
        this.toastService.show('Organización dada de baja', 'info');
        this.notificationService.notifyOrganizationStatusUpdate(org.id, 'SUSPENDIDO');
        this.cdr.detectChanges();
        this.closeDropdown();
      },
      error: (err) => {
        console.error('Error suspending organization', err);
        this.errorMessage = 'Error al dar de baja: ' + err.message;
      }
    });
  }

  openEditModal(org: Organization) {
    this.editingOrgId = org.id;
    // Backend update supports: name, type, email, phone, sector, scope, contactPerson, description, address, web
    // Note: frontend model 'Organization' might not have all these fields explicitly mapped in loadOrganizations initially,
    // so it's safer to fetch details or map them if available.
    // In loadOrganizations, we are mapping: type, phone, sector, scope, description.
    // 'contactPerson', 'address', 'web' were NOT mapped in loadOrganizations.
    // We should fetch full details or update loadOrganizations.
    // Let's fetch details for editing to be safe.

    this.apiService.getOrganization(org.id).subscribe({
      next: (fullOrg) => {
        this.editForm.patchValue({
          name: fullOrg.name,
          email: fullOrg.email,
          phone: fullOrg.phone,
          type: fullOrg.type,
          sector: fullOrg.sector,
          scope: fullOrg.scope,
          contactPerson: fullOrg.contactPerson,
          description: fullOrg.description,
          address: fullOrg.address,
          web: fullOrg.web,
          status: fullOrg.status
        });
        this.showEditModal = true;
        this.closeDropdown();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.toastService.show("Error al cargar datos para editar", "error");
      }
    });
  }

  closeEditModal() {
    this.showEditModal = false;
    this.editingOrgId = null;
    this.editForm.reset();
  }

  saveEdit() {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    if (this.editingOrgId) {
      this.apiService.updateOrganization(this.editingOrgId, this.editForm.value).subscribe({
        next: () => {
          this.toastService.show('Organización actualizada correctamente', 'success');
          this.closeEditModal();
          this.loadOrganizations();
        },
        error: (err) => {
          console.error('Error updating org', err);
          this.toastService.show('Error al actualizar: ' + (err.error?.error || 'Desconocido'), 'error');
        }
      });
    }
  }

  openCreateModal() {
    this.showCreateModal = true;
    this.createForm.reset({
      type: 'ONG',
      sector: 'SOCIAL',
      scope: 'LOCAL',
      password: '123456'
    });
  }

  closeCreateModal() {
    this.showCreateModal = false;
    this.createForm.reset();
  }

  saveCreate() {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }

    const payload = { ...this.createForm.value, role: 'admin' };
    this.apiService.registerOrganization(payload).subscribe({
      next: (res) => {
        this.toastService.show('Organización creada correctamente', 'success');
        this.closeCreateModal();
        this.loadOrganizations();
      },
      error: (err) => {
        console.error('Error creating organization', err);
        let errorMsg = 'Error desconocido';
        if (err.error?.errors) {
          errorMsg = Object.values(err.error.errors).join(', ');
        } else if (err.error?.error) {
          errorMsg = err.error.error;
        }
        this.toastService.show('Error al crear organización: ' + errorMsg, 'error');
      }
    });
  }
}

