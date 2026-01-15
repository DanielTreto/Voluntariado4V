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
  onOpenRegisterVol = output();

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

  constructor() {}

  closeModal(): void {
    this.onClose.emit();
  }

  openLoginModal(): void {
    this.onOpenLogin.emit();
  }

  openRegisterVolModal(): void {
    this.onOpenRegisterVol.emit();
  }

  registerOrganization(): void {
    this.apiService.registerOrganization(this.org).subscribe({
      next: (response) => {
        console.log('Organization registered', response);
        alert('Organización registrada con éxito');
        this.closeModal();
      },
      error: (error) => {
        console.error('Error registering org', error);
        alert(`Error Status: ${error.status}\nError Body: ${JSON.stringify(error.error)}`);
      },
    });
  }
}
