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

  userType: 'organization' | 'volunteer' = 'organization'; // Default to organization
  settingsForm!: FormGroup;
  loading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Mock ID for development (should come from Auth service)
  currentUserId = 1;

  ngOnInit() {
    this.initForm();
    this.loadData();
  }

  setUserType(type: 'organization' | 'volunteer') {
    this.userType = type;
    this.initForm(); // Re-init form with correct controls
    this.loadData();
    this.successMessage = null;
    this.errorMessage = null;
  }

  initForm() {
    if (this.userType === 'organization') {
      this.settingsForm = this.fb.group({
        name: ['', Validators.required],
        type: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
        sector: ['', Validators.required],
        scope: ['', Validators.required],
        contactPerson: [''],
        description: [''],
        // Photo URL placeholder logic - not persisted in DB yet as per schema
        photoUrl: ['']
      });
    } else {
      this.settingsForm = this.fb.group({
        name: ['', Validators.required],
        surname1: ['', Validators.required],
        surname2: [''],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
        dni: ['', [Validators.required, Validators.pattern(/^[0-9]{8}[A-Z]$/)]], // Basic DNI validation
        dateOfBirth: ['', Validators.required],
        course: [''],
        description: [''],
        photoUrl: ['']
      });
    }
  }

  loadData() {
    this.loading = true;
    this.settingsForm.disable();

    if (this.userType === 'organization') {
      this.apiService.getOrganization(this.currentUserId).subscribe({
        next: (data) => {
          this.settingsForm.patchValue(data);
          this.loading = false;
          this.settingsForm.enable();
        },
        error: (err) => {
          console.error('Error loading organization', err);
          this.errorMessage = 'Error al cargar los datos del usuario.';
          this.loading = false;
          this.settingsForm.enable();
        }
      });
    } else {
      this.apiService.getVolunteer(this.currentUserId).subscribe({
        next: (data) => {
          this.settingsForm.patchValue(data);
          this.loading = false;
          this.settingsForm.enable();
        },
        error: (err) => {
          console.error('Error loading volunteer', err);
          this.errorMessage = 'Error al cargar los datos del voluntario.';
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
    const data = this.settingsForm.value;

    const request$ = this.userType === 'organization'
      ? this.apiService.updateOrganization(this.currentUserId, data)
      : this.apiService.updateVolunteer(this.currentUserId, data);

    request$.subscribe({
      next: (res) => {
        this.successMessage = "¡Datos actualizados correctamente!";
        this.loading = false;
        // Optionally reload data to confirm
        // this.loadData();
      },
      error: (err) => {
        console.error('Error updating settings', err);
        this.errorMessage = err.error?.error || "Error al actualizar los datos.";
        if (err.error?.errors) {
          this.errorMessage += ' ' + JSON.stringify(err.error.errors);
        }
        this.loading = false;
      }
    });
  }
}
