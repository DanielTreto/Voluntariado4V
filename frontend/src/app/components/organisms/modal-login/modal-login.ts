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

  loginWithGoogle(): void {
    console.log('Initiating Google Login (Simulation)...');
    // Simulation: In the future, this will use AngularFireAuth to get the token
    const simulatedToken = "simulated_firebase_token_" + Math.random().toString(36).substr(2);
    
    this.apiService.login({ token: simulatedToken }).subscribe({
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
        alert('Login fallido (Simulación): ' + (error.error?.error || 'Error desconocido'));
      }
    });
  }

  // Legacy login method kept for reference but unused in UI
  login(): void {
     // implementation commented out or kept as is
  }

  openVolunteerRegister(): void {
    this.onRegisterVolClick.emit();
  }

  openOrgRegister(): void {
    this.onRegisterOrgClick.emit();
  }
}
