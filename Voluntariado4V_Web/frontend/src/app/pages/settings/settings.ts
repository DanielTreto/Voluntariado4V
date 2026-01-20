import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class SettingsComponent implements OnInit {
  private fb = inject(FormBuilder);
  private apiService = inject(ApiService);

  settingsForm!: FormGroup;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  selectedFile: File | null = null;
  avatarPreview: string | null = null;

  currentUserId: number | null = null;
  currentUserRole: string | null = null;
  ciclos: any[] = [];
  activityTypes: any[] = [];
  title: string = 'Ajustes de Perfil';

  ngOnInit() {
    this.initForm();

    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.currentUserId = user.id;
      this.currentUserRole = user.role || 'admin';
      this.title = this.currentUserRole === 'volunteer' ? 'Ajustes de Voluntario' : 'Ajustes de Administrador';
    } else {
      // Fallback
      this.currentUserId = 1; // Testing fallback
      this.currentUserRole = 'admin';
    }

    this.loadData();
  }

  initForm() {
    this.settingsForm = this.fb.group({
      name: ['', Validators.required],
      surname1: [''],
      surname2: [''],
      apellidos: [''], // For Admin
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
      photoUrl: [''],
      dni: ['', [Validators.pattern(/^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$/i)]],
      course: [''],
      description: [''],
      dateOfBirth: [''],
      preferences: [[]] // Array of IDs
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.avatarPreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  uploadAvatar() {
    if (!this.selectedFile || !this.currentUserId) return;

    if (this.currentUserRole === 'volunteer') {
      this.apiService.uploadVolunteerAvatar(this.currentUserId, this.selectedFile).subscribe({
        next: (res) => {
          // Update localStorage user object
          const user = JSON.parse(localStorage.getItem('user') || '{}');
          user.avatar = res.url;
          localStorage.setItem('user', JSON.stringify(user));

          this.successMessage = "Foto de perfil actualizada correctamente.";
          this.selectedFile = null;
        },
        error: (err) => {
          this.errorMessage = "Error al subir la imagen.";
          console.error(err);
        }
      });
    }
  }

  loadData() {
    if (!this.currentUserId) return;

    this.loading = true;
    this.settingsForm.disable();

    if (this.currentUserRole === 'volunteer') {
      // Load Volunteer Data
      this.apiService.getVolunteer(this.currentUserId!).subscribe({
        next: (data) => {
          this.settingsForm.patchValue(data);

          // Map avatar to photoUrl and handle prefix
          if (data.avatar) {
            if (data.avatar.startsWith('/uploads/')) {
              this.settingsForm.patchValue({ photoUrl: this.apiService.baseUrl + data.avatar });
            } else {
              this.settingsForm.patchValue({ photoUrl: data.avatar });
            }
          }

          // Set Preferences
          if (data.preferences) {
            this.settingsForm.patchValue({ preferences: data.preferences });
          }
          this.loading = false;
          this.settingsForm.enable();
        },
        error: (err) => {
          console.error('Error loading volunteer profile', err);
          this.errorMessage = 'Error al cargar los datos del voluntario.';
          this.loading = false;
          this.settingsForm.enable();
        }
      });

      // Load Ciclos
      this.apiService.getCiclos().subscribe({
        next: (res) => this.ciclos = res,
        error: (err) => console.error('Error loading ciclos', err)
      });

      // Load Activity Types
      this.apiService.getActivityTypes().subscribe({
        next: (res) => this.activityTypes = res,
        error: (err) => console.error('Error loading activity types', err)
      });

    } else if (this.currentUserRole === 'admin') {
      // Load Admin Data
      this.apiService.getAdmin(this.currentUserId!).subscribe({
        next: (data) => {
          // Map backend fields to form
          const formData = {
            name: data.name,
            apellidos: data.apellidos,
            email: data.email,
            phone: data.phone,
            photoUrl: data.avatar // Map avatar to form control for preview
          };
          this.settingsForm.patchValue(formData);
          this.loading = false;
          this.settingsForm.enable();
        },
        error: (err) => {
          console.error('Error loading admin profile', err);
          this.errorMessage = 'Error al cargar los datos del administrador.';
          this.loading = false;
          this.settingsForm.enable();
        }
      });
    }
  }

  onSubmit() {
    if (this.settingsForm.invalid) {
      this.errorMessage = "Por favor, revisa los campos marcados.";
      return;
    }

    this.loading = true;
    this.successMessage = null;
    this.errorMessage = null;

    // First update general data
    const data = this.settingsForm.value;

    if (this.currentUserRole === 'volunteer') {
      this.apiService.updateVolunteer(this.currentUserId!, data).subscribe({
        next: (res) => {
          if (this.selectedFile) {
            this.uploadAvatar();
          } else {
            this.successMessage = "¡Datos actualizados correctamente!";
          }

          // Update name in localStorage too if changed
          const user = JSON.parse(localStorage.getItem('user') || '{}');
          user.name = data.name;
          localStorage.setItem('user', JSON.stringify(user));

          this.loading = false;
        },
        error: (err) => {
          this.handleError(err);
        }
      });
    } else {
      // Admin update
      this.apiService.updateAdmin(this.currentUserId!, data).subscribe({
        next: (res) => {
          if (this.selectedFile) {
            this.uploadAvatar();
          } else {
            this.successMessage = "¡Datos actualizados correctamente!";
          }

          const user = JSON.parse(localStorage.getItem('user') || '{}');
          user.name = data.name;
          localStorage.setItem('user', JSON.stringify(user));

          this.loading = false;
        },
        error: (err) => {
          this.handleError(err);
        }
      });
    }
  }

  handleError(err: any) {
    console.error('Error updating settings', err);
    this.errorMessage = err.error?.error || "Error al actualizar los datos.";
    if (err.error?.errors) {
      this.errorMessage += ' ' + JSON.stringify(err.error.errors);
    }
    this.loading = false;
  }

  // Helper for checkbox group
  onPreferenceChange(e: any, typeId: number) {
    const preferences: number[] = this.settingsForm.get('preferences')?.value || [];
    if (e.target.checked) {
      if (!preferences.includes(typeId)) {
        preferences.push(typeId);
      }
    } else {
      const index = preferences.indexOf(typeId);
      if (index > -1) {
        preferences.splice(index, 1);
      }
    }
    this.settingsForm.get('preferences')?.setValue(preferences);
  }

  preferenceSelected(typeId: number): boolean {
    const preferences: number[] = this.settingsForm.get('preferences')?.value || [];
    return preferences.includes(typeId);
  }
}
