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
  onOpenRegisterOrg = output();

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

  globalError: string = '';
  submitting: boolean = false;
  errors: { [key: string]: string } = {};

  constructor() {}

  closeModal(): void {
    this.onClose.emit(); 
  }

  openLoginModal(): void {
    this.onOpenLogin.emit();
  }

  openRegisterOrgModal(): void {
    this.onOpenRegisterOrg.emit();
  }

  registerVolunteer(): void {
    this.globalError = '';
    this.submitting = true;

    // Basic validation is handled by template
    this.apiService.registerVolunteer(this.volunteer).subscribe({
      next: (response) => {
        console.log('Volunteer registered', response);
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
