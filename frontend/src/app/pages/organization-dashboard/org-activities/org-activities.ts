import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({
    selector: 'app-org-activities',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './org-activities.html',
    styleUrls: ['./org-activities.css']
})
export class OrgActivitiesComponent implements OnInit {
    currentTab: 'historial' | 'solicitud' = 'historial';
    activities: any[] = [];
    requestForm: FormGroup;

    // Lists for dropdowns (Ideally fetched from API, hardcoded for now or fetched if endpoints exist)
    odsList = [
        { id: 1, name: 'Fin de la Pobreza' },
        { id: 2, name: 'Hambre Cero' },
        { id: 3, name: 'Salud y Bienestar' },
        { id: 4, name: 'Educación de Calidad' },
        // Add more as needed
    ];

    activityTypes = [
        { id: 1, name: 'Social' },
        { id: 2, name: 'Ambiental' },
        { id: 3, name: 'Educativa' },
        { id: 4, name: 'Cultural' },
        { id: 5, name: 'Deportiva' }
        // Add more as needed
    ];

    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);

    constructor() {
        this.requestForm = this.fb.group({
            NOMBRE: ['', Validators.required],
            DESCRIPCION: ['', [Validators.required, Validators.maxLength(500)]],
            FECHA_INICIO: ['', Validators.required],
            FECHA_FIN: ['', Validators.required],
            DURACION_SESION: ['', Validators.required],
            N_MAX_VOLUNTARIOS: [1, [Validators.required, Validators.min(1)]],
            // For now assuming backend handles CODORG from logged in user or we send it
            CODTIPO: [null, Validators.required], // TODO: Bind to backend logic
            NUMODS: [null, Validators.required]
        });
    }

    ngOnInit(): void {
        this.loadActivities();
    }

    setTab(tab: 'historial' | 'solicitud') {
        this.currentTab = tab;
    }

    loadActivities() {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (!user || user.role !== 'organization') return;

        // Ideally use a specific endpoint getOrganizationActivities(user.id)
        // As seen in ApiService: getOrganizationActivities(orgId)
        if (user.id) {
            this.apiService.getOrganizationActivities(user.id).subscribe({
                next: (data) => {
                    this.activities = data;
                },
                error: (err) => console.error('Error loading activities', err)
            });
        }
    }

    submitRequest() {
        if (this.requestForm.invalid) {
            this.requestForm.markAllAsTouched();
            return;
        }

        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const formValue = this.requestForm.value;

        // Prepare payload matching backend entity expectation
        // Note: Backend expects CODORG (string from session/user ideally), CODTIPO (int), NUMODS (int or array?)
        // Need to verify how backend expects relations.
        // Based on previous debug, Actividad uses ManyToMany for ODS and Types.
        // The simple create endpoint might expect IDs.

        // Construct payload matching Backend ActivityController::create expectations
        const payload = {
            title: formValue.NOMBRE,
            description: formValue.DESCRIPCION,
            date: formValue.FECHA_INICIO,
            duration: formValue.DURACION_SESION,
            organizationId: user.id || 'org001',
            maxVolunteers: formValue.N_MAX_VOLUNTARIOS
        };

        console.log('Submitting activity:', payload);

        this.apiService.createActivity(payload).subscribe({
            next: (res) => {
                alert('Solicitud enviada con éxito');
                this.requestForm.reset();
                this.currentTab = 'historial';
                this.loadActivities();
            },
            error: (err) => {
                console.error('Error creating activity', err);
                alert('Error al enviar la solicitud. Revisa la consola.');
            }
        });
    }
}
