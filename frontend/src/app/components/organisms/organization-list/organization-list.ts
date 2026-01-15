import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BadgeComponent } from '../../atoms/badge/badge';
import { ApiService } from '../../../services/api.service';

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
  imports: [CommonModule, BadgeComponent, FormsModule],
  templateUrl: './organization-list.html',
  styleUrl: './organization-list.css'
})
export class OrganizationListComponent implements OnInit {
  private apiService = inject(ApiService);
  private cdr = inject(ChangeDetectorRef);
  activeTab: 'pending' | 'registered' = 'pending';
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
  loadingActivities: boolean = false;

  organizations: Organization[] = [];
  allActivities: Activity[] = [];

  ngOnInit() {
    this.loadOrganizations();
    this.loadAllActivities();
  }

  loadOrganizations() {
    this.apiService.getOrganizations().subscribe({
      next: (data) => {
        console.log('Organizations received:', data);
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
        this.errorMessage = 'Error loading data: ' + err.message;
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
          date: act.date,
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
        this.closeDropdown();
      },
      error: (err) => {
        console.error('Error suspending organization', err);
        this.errorMessage = 'Error al dar de baja: ' + err.message;
      }
    });
  }

  suspendOrg() {
    if (this.orgToSuspend) {
      this.apiService.updateOrganizationStatus(this.orgToSuspend.id, 'SUSPENDIDO').subscribe({
        next: () => {
          this.orgToSuspend!.status = 'suspended';
          this.orgToSuspend = null;
        },
        error: (err) => {
          console.error('Error suspending organization', err);
          this.errorMessage = 'Error al suspender organización: ' + err.message;
        }
      });
    }
  }
}
