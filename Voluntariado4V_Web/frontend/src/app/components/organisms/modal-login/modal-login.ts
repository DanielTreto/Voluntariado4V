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

  credentials = { email: '', password: '' };

  // Validation state
  loginError: string = '';
  fieldErrors = { email: false, password: false };
  submitting: boolean = false;

  constructor() { }

  closeModal(): void {
    this.resetState();
    this.onClose.emit();
  }

  resetState() {
    this.loginError = '';
    this.fieldErrors = { email: false, password: false };
    this.submitting = false;
    this.credentials = { email: '', password: '' };
  }

  loginWithGoogle(): void {
    this.resetState();
    this.submitting = true;

    const provider = new GoogleAuthProvider();
    signInWithPopup(this.auth, provider)
      .then(async (result: any) => {
        const user = result.user;
        const token = await user.getIdToken();
        const email = user.email || '';
        this.sendTokenToBackend(token, email);
      })
      .catch((error: any) => {
        this.submitting = false;
        console.error('Google Login Error', error);
        this.loginError = 'Error al iniciar sesión con Google: ' + error.message;
      });
  }

  loginWithEmail(): void {
    this.loginError = '';
    this.fieldErrors = { email: false, password: false };

    if (!this.credentials.email) this.fieldErrors.email = true;
    if (!this.credentials.password) this.fieldErrors.password = true;

    if (this.fieldErrors.email || this.fieldErrors.password) {
      return;
    }

    this.submitting = true;

    // 1. Try Firebase Login
    signInWithEmailAndPassword(this.auth, this.credentials.email, this.credentials.password)
      .then(async (userCredential: any) => {
        // Firebase Login Success
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
            this.submitting = false;
            console.error('Backend Login failed', backendError);
            // Highlight fields red
            this.fieldErrors.email = true;
            this.fieldErrors.password = true;
            this.loginError = 'Usuario o contraseña incorrectos.';
          }
        });
      });
  }

  private handleLoginSuccess(response: any) {

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
        this.submitting = false;
        console.error('Backend Login failed', error);
        this.loginError = 'Error de autenticación: ' + (error.error?.error || 'Inténtalo de nuevo.');
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
