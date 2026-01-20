import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
    selector: 'app-admin-settings',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './admin-settings.html',
    styles: [`
    .img-thumbnail { border: 3px solid #fff; box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15); }
    .btn-primary { background-color: #0d6efd; border: none; }
  `]
})
export class AdminSettingsComponent implements OnInit {
    settingsForm!: FormGroup;
    loading = true;
    successMessage: string | null = null;
    errorMessage: string | null = null;
    currentUserId: string | number | null = null;
    selectedFile: File | null = null;
    avatarPreview: string | ArrayBuffer | null = null;

    constructor(
        private fb: FormBuilder,
        private apiService: ApiService
    ) { }

    ngOnInit(): void {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            const user = JSON.parse(userStr);
            this.currentUserId = user.id;
        }

        this.initForm();
        this.loadData();
    }

    initForm() {
        this.settingsForm = this.fb.group({
            name: ['', Validators.required],
            apellidos: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            phone: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
            avatar: [''] // URL of current avatar
        });
    }

    loadData() {
        if (!this.currentUserId) return;

        this.apiService.getAdmin(Number(this.currentUserId)).subscribe({
            next: (data) => {
                // Map backend response specifically for Admin
                this.settingsForm.patchValue({
                    name: data.name,
                    apellidos: data.apellidos,
                    email: data.email,
                    phone: data.phone,
                    avatar: data.photoUrl || data.avatar // Handle both keys if backend inconsistent, currently uses 'avatar' in latest update
                });

                // Handle avatar preview if exists
                if (data.avatar && data.avatar.startsWith('/uploads')) {
                    this.settingsForm.patchValue({ avatar: this.apiService.baseUrl + data.avatar });
                }

                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading admin settings', err);
                this.errorMessage = 'Error al cargar los datos del administrador.';
                this.loading = false;
            }
        });
    }

    onFileSelected(event: any) {
        const file = event.target.files[0];
        if (file) {
            this.selectedFile = file;

            // Preview
            const reader = new FileReader();
            reader.onload = () => {
                this.avatarPreview = reader.result;
            };
            reader.readAsDataURL(file);
        }
    }

    onSubmit() {
        if (this.settingsForm.invalid) {
            this.errorMessage = "Por favor, revisa los campos requeridos.";
            return;
        }

        this.loading = true;
        this.successMessage = null;
        this.errorMessage = null;

        const data = this.settingsForm.value;

        this.apiService.updateAdmin(Number(this.currentUserId), data).subscribe({
            next: (res) => {
                if (this.selectedFile) {
                    this.uploadAvatar();
                } else {
                    this.finishUpdate(data);
                }
            },
            error: (err) => {
                console.error('Update error', err);
                this.errorMessage = "Error al actualizar los datos.";
                this.loading = false;
            }
        });
    }

    uploadAvatar() {
        if (!this.selectedFile || !this.currentUserId) return;

        this.apiService.uploadAdminAvatar(this.currentUserId, this.selectedFile).subscribe({
            next: (res) => {
                const user = JSON.parse(localStorage.getItem('user') || '{}');
                user.avatar = res.url; // Relative path usually
                localStorage.setItem('user', JSON.stringify(user));

                // Update form value with full URL for immediate preview without refresh if needed
                // But we have avatarPreview already.

                this.finishUpdate(this.settingsForm.value);
            },
            error: (err) => {
                console.error('Avatar upload error', err);
                this.errorMessage = "Datos actualizados, pero error al subir la foto.";
                this.loading = false;
            }
        });
    }

    finishUpdate(data: any) {
        this.successMessage = "¡Perfil de administrador actualizado correctamente!";
        this.loading = false;

        // Update local storage name too
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        user.name = data.name;
        localStorage.setItem('user', JSON.stringify(user));
    }
}
