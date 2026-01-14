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

  currentUserId: number | null = null;
  currentUserRole: string | null = null;

  ngOnInit() {
    this.initForm();

    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      this.currentUserId = user.id;
      this.currentUserRole = user.role || 'admin';
    } else {
      // Fallback
      this.currentUserId = 1;
      this.currentUserRole = 'admin';
    }

    this.loadData();
  }

  initForm() {
    this.settingsForm = this.fb.group({
      name: ['', Validators.required],
      apellidos: [''], // Optional for volunteer if we use surnames
      surname1: [''],
      surname2: [''],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
      photoUrl: ['']
    });
  }

  loadData() {
    this.loading = true;
    this.settingsForm.disable();

    if (this.currentUserRole === 'volunteer') {
      this.apiService.getVolunteer(this.currentUserId!).subscribe({
        next: (data) => {
          this.settingsForm.patchValue(data);
          // If form has 'apellidos' field but data has surnames, we might want to fill surnames
          // or if the UI uses apellidos, we map it. 
          // For now, let's assume the UI might need adjustment or we just accept what we have.
          // If the template uses 'apellidos', we should fill it.
          if (!data.apellidos && (data.surname1 || data.surname2)) {
             this.settingsForm.patchValue({
                 apellidos: `${data.surname1 || ''} ${data.surname2 || ''}`.trim()
             });
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
    } else {
      this.apiService.getAdmin(this.currentUserId!).subscribe({
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

    if (this.currentUserRole === 'volunteer') {
       // Check if we need to split apellidos back to surnames?
       // For now, let's send data as is, assuming backend handles or we only updated common fields.
       this.apiService.updateVolunteer(this.currentUserId!, data).subscribe({
        next: (res) => {
          this.successMessage = "¡Datos actualizados correctamente!";
          this.loading = false;
        },
        error: (err) => {
           this.handleError(err);
        }
       });
    } else {
       this.apiService.updateAdmin(this.currentUserId!, data).subscribe({
        next: (res) => {
          this.successMessage = "¡Datos actualizados correctamente!";
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
}
