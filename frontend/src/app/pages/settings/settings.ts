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

  // Hardcoded Admin ID for now (until Auth is fully integrated)
  currentAdminId = 1;

  ngOnInit() {
    this.initForm();
    this.loadData();
  }

  initForm() {
    this.settingsForm = this.fb.group({
      name: ['', Validators.required],
      apellidos: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
      // Photo URL placeholder logic - not persisted in DB as file upload yet
      photoUrl: ['']
    });
  }

  loadData() {
    this.loading = true;
    this.settingsForm.disable();

    this.apiService.getAdmin(this.currentAdminId).subscribe({
      next: (data) => {
        this.settingsForm.patchValue(data);
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

  onSubmit() {
    if (this.settingsForm.invalid) {
      this.errorMessage = "Por favor, revisa los campos marcados.";
      return;
    }

    this.loading = true;
    this.successMessage = null;
    this.errorMessage = null;
    const data = this.settingsForm.value;

    this.apiService.updateAdmin(this.currentAdminId, data).subscribe({
      next: (res) => {
        this.successMessage = "¡Datos actualizados correctamente!";
        this.loading = false;
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
