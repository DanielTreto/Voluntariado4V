import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-modal-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-login.html',
  styleUrl: './modal-login.scss',
})
export class ModalLogin {
  onModalClick = output(); 
  onRegisterVolClick = output();
  onRegisterOrgClick = output();
  onClose = output();

  private apiService = inject(ApiService);
  private router = inject(Router);

  credentials = {
    email: '',
    password: ''
  };

  constructor() {}

  closeModal(): void {
    this.onClose.emit();
  }

  login(): void {
    this.apiService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login successful', response);
        localStorage.setItem('user', JSON.stringify(response));
        this.onModalClick.emit();
        if (response.role === 'volunteer') {
          this.router.navigate(['/volunteer-dashboard']);
        } else if (response.role === 'organization') {
          this.router.navigate(['/organization-dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
        this.closeModal();
      },
      error: (error) => {
        console.error('Login failed', error);
        alert('Login fallido: ' + (error.error?.error || 'Credenciales incorrectas'));
      }
    });
  }

  openVolunteerRegister(): void {
    this.onRegisterVolClick.emit();
  }

  openOrgRegister(): void {
    this.onRegisterOrgClick.emit();
  }
}
