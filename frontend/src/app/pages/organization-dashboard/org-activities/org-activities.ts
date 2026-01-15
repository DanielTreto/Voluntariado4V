import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { AvatarComponent } from '../../../components/atoms/avatar/avatar';
import { BadgeComponent } from '../../../components/atoms/badge/badge';

import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-org-activities',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, AvatarComponent],
    templateUrl: './org-activities.html',
    styleUrls: ['./org-activities.css']
})
export class OrgActivitiesComponent implements OnInit {
    currentTab: 'historial' | 'solicitud' | 'solicitudes' = 'historial';
    activities: any[] = [];
    requests: any[] = [];
    pendingRequestsCount: number = 0;
    requestForm: FormGroup;

    // Modal state
    showViewModal: boolean = false;
    viewActivity: any = null;

    // Lists for dropdowns
    odsList = [
        { id: 1, name: 'Fin de la Pobreza' },
        { id: 2, name: 'Hambre Cero' },
        { id: 3, name: 'Salud y Bienestar' },
        { id: 4, name: 'Educación de Calidad' },
    ];

    activityTypes = [
        { id: 1, name: 'Social' },
        { id: 2, name: 'Ambiental' },
        { id: 3, name: 'Educativa' },
        { id: 4, name: 'Cultural' },
        { id: 5, name: 'Deportiva' }
    ];

    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);
    private route = inject(ActivatedRoute);
    private cdr = inject(ChangeDetectorRef);

    constructor() {
        this.requestForm = this.fb.group({
            NOMBRE: ['', Validators.required],
            DESCRIPCION: ['', [Validators.required, Validators.maxLength(500)]],
            UBICACION: ['', Validators.required],
            FECHA_INICIO: ['', Validators.required],
            FECHA_FIN: ['', Validators.required],
            DURACION_SESION: ['', Validators.required],
            N_MAX_VOLUNTARIOS: [1, [Validators.required, Validators.min(1)]],
            CODTIPO: [null, Validators.required],
            NUMODS: [null, Validators.required]
        });
    }

    ngOnInit(): void {
        this.loadActivities();
        this.loadRequests(); // Load initial count
    }

    setTab(tab: 'historial' | 'solicitud' | 'solicitudes') {
        this.currentTab = tab;
        if (tab === 'historial') {
            this.loadActivities();
        } else if (tab === 'solicitudes') {
            this.loadRequests();
        }
    }

    loadRequests() {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (!user || user.role !== 'organization') return;

        this.apiService.getOrganizationRequests(user.id).subscribe({
            next: (data) => {
                this.requests = data;
                this.pendingRequestsCount = data.filter(r => r.status === 'PENDIENTE').length;
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
                this.cdr.detectChanges();
                this.loadRequests(); // Refresh list/count
                alert(`Solicitud ${status === 'ACEPTADA' ? 'aceptada' : 'rechazada'} correctamente.`);
            },
            error: (err) => {
                console.error('Error updating status', err);
                alert(err.error?.error || 'Error al actualizar el estado.');
            }
        });
    }

    loadActivities() {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (!user || user.role !== 'organization') return;

        if (user.id) {
            this.apiService.getOrganizationActivities(user.id).subscribe({
                next: (data) => {
                    // Filter out PENDIENTE status as requested
                    this.activities = data.filter((act: any) =>
                        (act.ESTADO || act.status) !== 'PENDIENTE'
                    ).map((act: any) => ({
                        ...act,
                        title: act.NOMBRE || act.title,
                        description: act.DESCRIPCION || act.description,
                        date: act.FECHA_INICIO || act.date,
                        status: act.ESTADO || act.status,
                        image: act.image || 'assets/images/activity-1.jpg',
                        volunteers: act.volunteers || [],
                        type: act.type || 'General',
                        location: act.UBICACION || act.location
                    }));

                    this.cdr.detectChanges();

                    // Check for deep link to open modal
                    this.route.queryParams.subscribe(params => {
                        const openId = params['openId'];
                        if (openId) {
                            const activity = this.activities.find(a => a.id == openId);
                            if (activity) {
                                this.openViewDetails(activity);
                                // Optional: Clear param so reload doesn't reopen? 
                                // For now leaving it is fine or user might prefer it deep linkable.
                            }
                        }
                    });
                },
                error: (err) => console.error('Error loading activities', err)
            });
        }
    }

    openViewDetails(activity: any) {
        console.log('Opening details for:', activity);
        this.viewActivity = activity;
        this.showViewModal = true;
    }

    closeViewModal() {
        this.showViewModal = false;
        this.viewActivity = null;
    }

    submitRequest() {
        if (this.requestForm.invalid) {
            this.requestForm.markAllAsTouched();
            return;
        }

        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const formValue = this.requestForm.value;

        const payload = {
            title: formValue.NOMBRE,
            description: formValue.DESCRIPCION,
            location: formValue.UBICACION,
            date: formValue.FECHA_INICIO,
            duration: formValue.DURACION_SESION,
            organizationId: user.id,
            maxVolunteers: formValue.N_MAX_VOLUNTARIOS
        };

        console.log('Submitting activity:', payload);

        this.apiService.createActivity(payload).subscribe({
            next: (res) => {
                alert('Solicitud enviada con éxito. Pendiente de aprobación.');
                this.requestForm.reset();
                this.currentTab = 'historial';
                this.loadActivities(); // Refresh list (new one won't show until approved)
            },
            error: (err) => {
                console.error('Error creating activity', err);
                alert('Error al enviar la solicitud.');
            }
        });
    }
}
