import { CommonModule } from '@angular/common';
import { Component, inject, output, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-modal-register-vol',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-register-vol.html',
  styleUrl: './modal-register-vol.scss',
})
export class ModalRegisterVol implements OnInit {
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

    password: '',
    preferences: [] as number[],
    availability: [] as any[]
  };

  globalError: string = '';
  submitting: boolean = false;
  errors: { [key: string]: string } = {};
  cycles: any[] = [];
  activityTypes: any[] = [];

  // Availability Logic
  days: string[] = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'];
  availabilityMap: { [key: string]: number } = {};

  constructor() { }

  ngOnInit() {
    this.apiService.getCiclos().subscribe({
      next: (data) => this.cycles = data,
      error: (e) => console.error('Error fetching cycles', e)
    });

    this.apiService.getActivityTypes().subscribe({
      next: (data) => this.activityTypes = data,
      error: (e) => console.error('Error fetching activity types', e)
    });

    // Init availability map
    this.days.forEach(day => this.availabilityMap[day] = 0);
  }

  closeModal(): void {
    this.onClose.emit();
  }

  openLoginModal(): void {
    this.onOpenLogin.emit();
  }

  openRegisterOrgModal(): void {
    this.onOpenRegisterOrg.emit();
  }

  validateForm(): boolean {
    return true;
  }

  // Preference Helper
  togglePreference(typeId: number, event: any) {
    if (event.target.checked) {
      if (!this.volunteer.preferences.includes(typeId)) {
        this.volunteer.preferences.push(typeId);
      }
    } else {
      const index = this.volunteer.preferences.indexOf(typeId);
      if (index > -1) {
        this.volunteer.preferences.splice(index, 1);
      }
    }
  }

  registerVolunteer(): void {
    this.globalError = '';
    this.submitting = true;

    // Format Availability for Backend
    const formattedAvailability = this.days.map(day => ({
      day: day,
      hours: this.availabilityMap[day] || 0
    })).filter(a => a.hours >= 0); // Include 0 hours? Requirement says "cuantas horas". Usually 0 means not available. 
    // If I send 0, backend stores 0. Let's send all.

    this.volunteer.availability = formattedAvailability;

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
