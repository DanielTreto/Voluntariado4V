import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BadgeComponent } from '../../atoms/badge/badge';
import { Router } from '@angular/router';

@Component({
  selector: 'app-event-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-calendar.html',
  styleUrl: './event-calendar.css'
})
export class EventCalendarComponent {
  constructor(private router: Router) { }

  currentMonth = 'Diciembre 2025';
  weekDays = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];

  // Days of December 2025 (Starts on Monday)
  days = Array.from({ length: 31 }, (_, i) => i + 1);

  // Events matching active activities
  events: { [key: number]: { title: string, type: string }[] } = {
    5: [{ title: 'Limpieza de Playa', type: 'success' }],      // Medio Ambiente -> success (green)
    10: [{ title: 'Taller de Programación', type: 'primary' }], // Tecnológico -> primary (blue)
    15: [{ title: 'Reparto de Alimentos', type: 'warning' }]    // Social -> warning (yellow)
  };

  getEventsForDay(day: number) {
    return this.events[day] || [];
  }

  navigateToActivities() {
    this.router.navigate(['/activities']);
  }
}
