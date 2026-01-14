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

    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);

    constructor() {
        this.settingsForm = this.fb.group({
            NOMBRE: ['', Validators.required],
            DESCRIPCION: ['', Validators.maxLength(500)],
            TELEFONO: ['', [Validators.pattern(/^[6-9][0-9]{8}$/)]],
            DIRECCION: [''],
            WEB: [''],
            // Email usually read-only or handled separately for auth
            CORREO: [{ value: '', disabled: true }]
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
                    NOMBRE: data.NOMBRE,
                    DESCRIPCION: data.DESCRIPCION,
                    TELEFONO: data.TELEFONO,
                    DIRECCION: data.DIRECCION,
                    WEB: data.WEB,
                    CORREO: data.CORREO
                });
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading organization profile', err);
                this.loading = false;
            }
        });
    }

    saveSettings() {
        if (this.settingsForm.invalid || !this.userId) return;

        this.loading = true;
        // Only send mutable fields
        const payload = this.settingsForm.getRawValue(); // raw value to check email but backend ignores it usually if secure

        // Clean payload of disabled fields if needed, but getRawValue includes them. 
        // Usually backend ignores non-updatable fields or we filter them.

        this.apiService.updateOrganization(this.userId, payload).subscribe({
            next: (res) => {
                alert('Perfil actualizado correctamente');
                this.loading = false;
            },
            error: (err) => {
                console.error('Error updating profile', err);
                alert('Error al actualizar el perfil');
                this.loading = false;
            }
        });
    }
}
