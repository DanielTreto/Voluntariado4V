import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-modal-register-vol',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-register-vol.html',
  styleUrl: './modal-register-vol.scss',
})
export class ModalRegisterVol {
  onClose = output();
  onOpenLogin = output();

  private apiService = inject(ApiService);

  volunteer = {
    name: '',
    surname1: '',
    surname2: '',
    email: '',
    phone: '',
    dni: '',
    dateOfBirth: '',
    description: '',
    course: '',
    password: ''
  };

  errors: { [key: string]: string } = {};
  globalError: string = '';
  submitting: boolean = false;

  constructor() { }

  closeModal(): void {
    this.onClose.emit();
  }

  openLoginModal(): void {
    this.onOpenLogin.emit();
  }

  validateForm(): boolean {
    this.errors = {};
    let isValid = true;

    const requiredFields = ['name', 'surname1', 'email', 'phone', 'dni', 'dateOfBirth', 'password', 'course'];

    requiredFields.forEach(field => {
      if (!(this.volunteer as any)[field]) {
        this.errors[field] = 'Este campo es obligatorio';
        isValid = false;
      }
    });

    if (this.volunteer.email && !this.isValidEmail(this.volunteer.email)) {
      this.errors['email'] = 'Formato de correo inválido';
      isValid = false;
    }

    return isValid;
  }

  isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  registerVolunteer(): void {
    this.globalError = '';

    if (!this.validateForm()) {
      return;
    }

    this.submitting = true;

    this.apiService.registerVolunteer(this.volunteer).subscribe({
      next: (response) => {
        console.log('Volunteer registered', response);
        // Success feedback? Maybe just close or show success message inline?
        // User asked to remove alerts.
        this.closeModal();
      },
      error: (error) => {
        this.submitting = false;
        console.error('Error registering volunteer', error);

        let msg = 'Error en el registro. Inténtalo de nuevo.';
        if (error.error && error.error.error) {
          msg = error.error.error;
        }

        // Detect duplicates
        if (msg.includes('Duplicate') || msg.includes('SQLSTATE[23000]')) {
          if (msg.includes('CORREO')) {
            this.globalError = 'El correo electrónico ya está registrado.';
          } else if (msg.includes('DNI')) {
            this.globalError = 'El DNI ya está registrado.';
          } else {
            this.globalError = 'Ya existe un usuario con estos datos (DNI o Correo).';
          }
        } else {
          this.globalError = msg;
        }
      }
    });
  }
}
