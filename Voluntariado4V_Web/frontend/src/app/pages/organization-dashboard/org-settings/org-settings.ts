import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({
    selector: 'app-org-settings',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './org-settings.html',
    styleUrls: ['./org-settings.css']
})
export class OrgSettingsComponent implements OnInit {
    settingsForm: FormGroup;
    userId: number | null = null;
    loading = false;
    selectedFile: File | null = null;
    logoPreview: string | null = null;
    currentLogo: string | null = null;

    // Lists for dropdowns matching backend constraints
    sectores = [
        { value: 'SOCIAL', label: 'Social' },
        { value: 'SALUD', label: 'Salud' },
        { value: 'EDUCATIVO', label: 'Educativo' },
        { value: 'AMBIENTAL', label: 'Ambiental' },
        { value: 'CULTURAL', label: 'Cultural' },
        { value: 'DEPORTIVO', label: 'Deportivo' },
        { value: 'TECNOLÓGICO', label: 'Tecnológico' },
        { value: 'OTRO', label: 'Otro' }
    ];

    ambitos = [
        { value: 'LOCAL', label: 'Local' },
        { value: 'REGIONAL', label: 'Regional / Autonómico' },
        { value: 'NACIONAL', label: 'Nacional / Estatal' },
        { value: 'INTERNACIONAL', label: 'Internacional' }
    ];

    tiposOrganizacion = [
        { value: 'ONG', label: 'ONG' },
        { value: 'FUNDACIÓN', label: 'Fundación' },
        { value: 'ASOCIACIÓN', label: 'Asociación' },
        { value: 'ENTIDAD PÚBLICA', label: 'Entidad Pública' },
        { value: 'OTRA', label: 'Otra' }
    ];

    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);

    constructor() {
        this.settingsForm = this.fb.group({
            NOMBRE: ['', Validators.required],
            DESCRIPCION: ['', Validators.maxLength(500)],
            TELEFONO: ['', [Validators.pattern(/^[6-9][0-9]{8}$/)]],
            DIRECCION: [''],
            WEB: [''],
            CORREO: [{ value: '', disabled: true }],
            SECTOR: [null], // Changed to match select
            AMBITO: ['NACIONAL'], // Default value valid choice
            PERSONA_CONTACTO: [''],
            TIPO_ORG: [null]
        });
    }

    ngOnInit(): void {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (user && user.role === 'organization') {
            this.userId = user.id;
            this.loadProfile();
        }
    }

    loadProfile() {
        if (!this.userId) return;
        this.loading = true;

        this.apiService.getOrganization(this.userId).subscribe({
            next: (data) => {
                this.settingsForm.patchValue({
                    NOMBRE: data.name,
                    DESCRIPCION: data.description,
                    TELEFONO: data.phone,
                    DIRECCION: data.address,
                    WEB: data.web,
                    CORREO: data.email,
                    SECTOR: data.sector,
                    AMBITO: data.scope,
                    PERSONA_CONTACTO: data.contactPerson,
                    TIPO_ORG: data.type
                });

                // Update localStorage with fresh data for header consistency
                const user = JSON.parse(localStorage.getItem('user') || '{}');
                if (user) {
                    user.name = data.name;
                    if (data.avatar) {
                        user.avatar = data.avatar;
                    }
                    localStorage.setItem('user', JSON.stringify(user));
                }

                if (data.avatar) {
                    if (data.avatar.startsWith('/uploads/')) {
                        this.currentLogo = this.apiService.baseUrl + data.avatar;
                    } else {
                        this.currentLogo = data.avatar;
                    }
                }

                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading organization profile', err);
                this.loading = false;
            }
        });
    }

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.selectedFile = file;
            const reader = new FileReader();
            reader.onload = () => {
                this.logoPreview = reader.result as string;
            };
            reader.readAsDataURL(file);
        }
    }

    uploadLogo() {
        if (!this.selectedFile || !this.userId) return;

        this.apiService.uploadOrganizationAvatar(this.userId, this.selectedFile).subscribe({
            next: (res) => {
                const user = JSON.parse(localStorage.getItem('user') || '{}');
                user.avatar = res.url;
                localStorage.setItem('user', JSON.stringify(user));
                this.currentLogo = this.apiService.baseUrl + res.url;
                this.selectedFile = null;
                alert('Logo actualizado correctamente');
            },
            error: (err) => {
                console.error('Error uploading logo', err);
                alert('Error al subir el logo');
            }
        });
    }

    saveSettings() {
        if (this.settingsForm.invalid || !this.userId) return;

        this.loading = true;
        // Map to lowercase keys expected by backend
        const formValues = this.settingsForm.getRawValue();
        const payload = {
            name: formValues.NOMBRE,
            type: formValues.TIPO_ORG,
            email: formValues.CORREO,
            phone: formValues.TELEFONO,
            sector: formValues.SECTOR,
            scope: formValues.AMBITO,
            contactPerson: formValues.PERSONA_CONTACTO,
            description: formValues.DESCRIPCION,
            address: formValues.DIRECCION,
            web: formValues.WEB
        };

        this.apiService.updateOrganization(this.userId, payload).subscribe({
            next: (res) => {
                if (this.selectedFile) {
                    this.uploadLogo();
                } else {
                    alert('Perfil actualizado correctamente');
                }

                // Update name in localStorage too
                const user = JSON.parse(localStorage.getItem('user') || '{}');
                user.name = formValues.NOMBRE;
                localStorage.setItem('user', JSON.stringify(user));

                this.loading = false;
            },
            error: (err) => {
                console.error('Error updating profile', err);
                this.loading = false;

                if (err.error && err.error.errors) {
                    const messages = Object.values(err.error.errors).join('\n');
                    alert('Error:\n' + messages);
                } else if (err.error && err.error.error) {
                    alert('Error: ' + err.error.error);
                } else {
                    alert('Error al actualizar el perfil');
                }
            }
        });
    }
}
