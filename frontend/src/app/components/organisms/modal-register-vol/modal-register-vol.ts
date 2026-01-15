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

  registerError: string | null = null;
  registerSuccess: boolean = false;

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
    this.registerError = null;
    this.registerSuccess = false;

    // Basic validation is handled by template
    this.apiService.registerVolunteer(this.volunteer).subscribe({
      next: (response) => {
        console.log('Volunteer registered', response);
        this.registerSuccess = true;
        setTimeout(() => this.closeModal(), 2000); // Close after success message
      },
      error: (error) => {
        console.error('Error registering volunteer', error);
        this.registerError = `Error: ${error.error?.message || 'Error en el registro. Verifique los datos.'}`;
      }
    });
  }
}
