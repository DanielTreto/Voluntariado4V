import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-modal-register-org',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-register-org.html',
  styleUrl: './modal-register-org.scss',
})
export class ModalRegisterOrg {
  onClose = output();
  onOpenLogin = output();

  private apiService = inject(ApiService);

  org = {
    name: '',
    type: 'ONG', // Default
    email: '',
    phone: '',
    contactPerson: '',
    sector: 'SOCIAL', // Default
    scope: 'LOCAL', // Default
    description: '',
    password: '',
  };
  errors: { [key: string]: string } = {};
  globalError: string = '';
  submitting: boolean = false;

  constructor() {}

  closeModal(): void {
    this.onClose.emit();
  }

  openLoginModal(): void {
    this.onOpenLogin.emit();
  }

  validateForm(): boolean {
    this.errors = {};
    let isValid = true;
    
    // Required fields check, adjust as needed
    const requiredFields = ['name', 'type', 'sector', 'scope', 'phone', 'email', 'password', 'description'];
    
    requiredFields.forEach(field => {
      if (!(this.org as any)[field]) {
        this.errors[field] = 'Este campo es obligatorio';
        isValid = false;
      }
    });

    if (this.org.email && !this.isValidEmail(this.org.email)) {
      this.errors['email'] = 'Formato de correo inválido';
      isValid = false;
    }

    return isValid;
  }

  isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  registerOrganization(): void {
    this.globalError = '';

    if (!this.validateForm()) {
        return;
    }

    this.submitting = true;

    this.apiService.registerOrganization(this.org).subscribe({
      next: (response) => {
        console.log('Organization registered', response);
        // alert('Organización registrada con éxito');
        this.closeModal();
      },
      error: (error) => {
        this.submitting = false;
        console.error('Error registering org', error);
        
        let msg = 'Error en el registro. Inténtalo de nuevo.';
        if (error.error && error.error.error) {
             msg = error.error.error;
        }

        // Detect duplicates
        if (msg.includes('Duplicate') || msg.includes('SQLSTATE[23000]')) {
             if (msg.includes('CORREO')) {
                 this.globalError = 'El correo electrónico ya está registrado.';
             } else {
                 this.globalError = 'Ya existe una organización con estos datos.';
             }
        } else {
            this.globalError = msg;
        }
      },
    });
  }
}
