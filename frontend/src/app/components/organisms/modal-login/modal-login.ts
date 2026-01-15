import { CommonModule } from '@angular/common';
import { Component, inject, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Auth, GoogleAuthProvider, signInWithPopup, signInWithEmailAndPassword } from '@angular/fire/auth';

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
  private auth = inject(Auth);

  credentials = {
    email: '',
    password: ''
  };

  constructor() { }

  closeModal(): void {
    this.onClose.emit();
  }

  loginWithGoogle(): void {
    console.log('Initiating Google Login...');
    const provider = new GoogleAuthProvider();
    signInWithPopup(this.auth, provider)
      .then(async (result) => {
        const user = result.user;
        const token = await user.getIdToken();
        const email = user.email || ''; // Capture email
        this.sendTokenToBackend(token, email);
      })
      .catch((error) => {
        console.error('Google Login Error', error);
        alert('Google Login failed: ' + error.message);
      });
  }

  loginWithEmail(): void {
    console.log('Initiating Email Login...');
    signInWithEmailAndPassword(this.auth, this.credentials.email, this.credentials.password)
      .then(async (userCredential) => {
        const user = userCredential.user;
        const token = await user.getIdToken();
        this.sendTokenToBackend(token);
      })
      .catch((error: any) => {
        console.warn('Firebase Login Error, attempting direct backend login...', error);
        // Fallback or Primary for non-Firebase users: Attempt direct SQL login
        this.apiService.login({
          email: this.credentials.email,
          password: this.credentials.password
        }).subscribe({
          next: (response) => {
            this.handleLoginSuccess(response);
          },
          error: (backendError) => {
            console.error('Backend Login failed', backendError);
            alert('Login failed: ' + (backendError.error?.error || error.message));
          }
        });
      });
  }

  private handleLoginSuccess(response: any) {
    console.log('Login successful', response);
    localStorage.setItem('user', JSON.stringify(response));
    this.onModalClick.emit();
    // Redirect based on role
    if (response.role === 'volunteer') {
      this.router.navigate(['/volunteer-dashboard']);
    } else if (response.role === 'organization') {
      this.router.navigate(['/organization-dashboard']);
    } else {
      this.router.navigate(['/dashboard']);
    }
    this.closeModal();
  }

  private sendTokenToBackend(token: string, email: string = '') {
    this.apiService.login({ token, email }).subscribe({
      next: (response) => {
        this.handleLoginSuccess(response);
      },
      error: (error) => {
        console.error('Backend Login failed', error);
        alert('Backend Login failed: ' + (error.error?.error || 'Unknown error'));
      }
    });
  }

  login(): void {
    this.loginWithEmail();
  }

  openVolunteerRegister(): void {
    this.onRegisterVolClick.emit();
  }

  openOrgRegister(): void {
    this.onRegisterOrgClick.emit();
  }
}
