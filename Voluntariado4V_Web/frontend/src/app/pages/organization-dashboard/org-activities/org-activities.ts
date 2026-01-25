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
    currentTab: 'historial' | 'solicitud' = 'historial';
    activities: any[] = [];
    requestForm: FormGroup;

    // Modal state
    showViewModal: boolean = false;
    viewActivity: any = null;
    selectedFile: File | null = null;
    imagePreview: string | null = null;

    // Lists for dropdowns
    odsList = [
        { id: 1, name: 'Fin de la Pobreza' },
        { id: 2, name: 'Hambre Cero' },
        { id: 3, name: 'Salud y Bienestar' },
        { id: 4, name: 'Educación de Calidad' },
    ];

    activityTypes = [
        { id: 1, name: 'Digital' },
        { id: 2, name: 'Salud' },
        { id: 3, name: 'Educativo' },
        { id: 4, name: 'Ambiental' },
        { id: 5, name: 'Deportivo' },
        { id: 6, name: 'Social' },
        { id: 7, name: 'Cultural' },
        { id: 8, name: 'Tecnico' }
    ];

    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);
    private route = inject(ActivatedRoute);
    private cdr = inject(ChangeDetectorRef);
    private initialLoad = true;

    constructor() {
        this.requestForm = this.fb.group({
            NOMBRE: ['', Validators.required],
            DESCRIPCION: ['', [Validators.required, Validators.maxLength(500)]],
            UBICACION: ['', Validators.required],
            // IMAGEN field removed from form control, handled by file input
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
    }



    setTab(tab: 'historial' | 'solicitud') {
        this.currentTab = tab;
        if (tab === 'historial') {
            this.loadActivities();
        }
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
                        image: act.image ? (act.image.startsWith('/uploads') ? this.apiService.baseUrl + act.image : act.image) : 'assets/images/activity-1.jpg',
                        volunteers: act.volunteers || [],
                        type: act.type || 'General',
                        location: act.UBICACION || act.location
                    }));

                    this.cdr.detectChanges();

                    this.cdr.detectChanges();

                    // Check for deep link ONLY on initial load
                    if (this.initialLoad) {
                        const openId = this.route.snapshot.queryParams['openId'];
                        if (openId) {
                            const activity = this.activities.find(a => a.id == openId);
                            if (activity) {
                                // Defer the checking to avoid NG0100
                                setTimeout(() => this.openViewDetails(activity));
                            }
                        }
                        this.initialLoad = false;
                    }
                },
                error: (err) => console.error('Error loading activities', err)
            });
        }
    }

    openViewDetails(activity: any) {

        this.viewActivity = activity;
        this.showViewModal = true;
        this.cdr.detectChanges();
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

    closeViewModal() {
        this.showViewModal = false;
        this.viewActivity = null;
    }

    submitRequest() {
        if (this.requestForm.invalid) {
            this.requestForm.markAllAsTouched();
            return;
        }

        if (!this.selectedFile) {
            alert('Por favor, selecciona una imagen para la actividad.');
            return;
        }

        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const formValue = this.requestForm.value;

        const payload = {
            title: formValue.NOMBRE,
            description: formValue.DESCRIPCION,
            location: formValue.UBICACION,
            // image: handled separately
            date: formValue.FECHA_INICIO,
            duration: formValue.DURACION_SESION,
            organizationId: user.id,
            maxVolunteers: formValue.N_MAX_VOLUNTARIOS,
            typeId: formValue.CODTIPO
        };

        this.apiService.createActivity(payload).subscribe({
            next: (res) => {
                const activityId = res.id;

                // Now upload image
                if (this.selectedFile) {
                    this.apiService.uploadActivityImage(activityId, this.selectedFile).subscribe({
                        next: () => {
                            alert('Solicitud enviada con éxito. Pendiente de aprobación.');
                            this.resetForm();
                            this.currentTab = 'historial';
                            this.loadActivities();
                        },
                        error: (err) => {
                            console.error('Error uploading image', err);
                            alert('Actividad creada, pero hubo un error al subir la imagen.');
                            this.resetForm();
                            this.currentTab = 'historial';
                            this.loadActivities();
                        }
                    });
                } else {
                    // Should not happen due to check above, but as fallback
                    alert('Solicitud enviada con éxito. Pendiente de aprobación.');
                    this.resetForm();
                    this.currentTab = 'historial';
                    this.loadActivities();
                }
            },
            error: (err) => {
                console.error('Error creating activity', err);
                alert('Error al enviar la solicitud.');
            }
        });
    }

    resetForm() {
        this.requestForm.reset();
        this.selectedFile = null;
        this.imagePreview = null;
    }

    handleImageError(event: any) {
        event.target.src = 'https://blog.vicensvives.com/wp-content/uploads/2019/12/Voluntariado.png';
    }
}
