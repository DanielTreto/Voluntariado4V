import { Component, OnInit, inject, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BadgeComponent } from '../../atoms/badge/badge';
import { Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-event-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-calendar.html',
  styleUrl: './event-calendar.css'
})
export class EventCalendarComponent implements OnInit {
  private router = inject(Router);
  private apiService = inject(ApiService);

  currentDate: Date = new Date();
  monthLabel: string = '';
  weekDays = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
  calendarDays: any[] = [];
  activities: any[] = [];

  // Day Details Modal
  selectedDay: any = null;
  dayActivities: any[] = [];
  showDayModal: boolean = false;

  @Input() organizationId: number | null = null;

  ngOnInit() {
    this.updateMonthLabel();

    // Auto-detect if Organization and not explicitly passed
    if (!this.organizationId) {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      if (user && user.role === 'organization') {
        this.organizationId = user.id;
      }
    }

    this.loadActivities();
  }

  loadActivities() {
    if (this.organizationId) {
      this.apiService.getOrganizationActivities(this.organizationId).subscribe({
        next: (data) => {
          this.activities = data;
          this.generateCalendar();
        },
        error: (err) => console.error('Error loading organization activities', err)
      });
    } else {
      this.apiService.getActivities().subscribe({
        next: (data) => {
          this.activities = data;
          this.generateCalendar();
        },
        error: (err) => console.error('Error loading activities', err)
      });
    }
  }

  generateCalendar() {
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    // First day of the month
    const firstDay = new Date(year, month, 1);
    // Last day of the month
    const lastDay = new Date(year, month + 1, 0);

    // Days in month
    const daysInMonth = lastDay.getDate();

    // Day of week of first day (0=Sunday, 1=Monday... but we want 1=Monday, 7=Sunday)
    let startingDay = firstDay.getDay();
    if (startingDay === 0) startingDay = 7; // Adjust Sunday to 7

    this.calendarDays = [];

    // Previous month padding
    for (let i = 1; i < startingDay; i++) {
      this.calendarDays.push({ day: '', events: [], empty: true });
    }

    // Current month days
    for (let i = 1; i <= daysInMonth; i++) {
      const dateString = `${year}-${(month + 1).toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`;
      const dayEvents = this.activities.filter(a => a.date === dateString);

      this.calendarDays.push({
        day: i,
        date: dateString,
        events: dayEvents,
        empty: false
      });
    }
  }

  updateMonthLabel() {
    const months = [
      'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];
    this.monthLabel = `${months[this.currentDate.getMonth()]} ${this.currentDate.getFullYear()}`;
  }

  prevMonth() {
    this.currentDate.setMonth(this.currentDate.getMonth() - 1);
    this.updateMonthLabel();
    this.generateCalendar();
  }

  nextMonth() {
    this.currentDate.setMonth(this.currentDate.getMonth() + 1);
    this.updateMonthLabel();
    this.generateCalendar();
  }

  openDayDetails(dayParam: any) {
    if (dayParam.empty || dayParam.events.length === 0) return;
    this.selectedDay = dayParam;
    this.dayActivities = dayParam.events;
    this.showDayModal = true;
  }

  closeDayModal() {
    this.showDayModal = false;
    this.selectedDay = null;
  }

  navigateToActivity(activity: any) {
    // Navigate to activities page with query param to open this activity
    this.router.navigate(['/dashboard/activities'], { queryParams: { openId: activity.id } });
  }

  getEventTypeClass(type: string): string {
    const classes: any = {
      'Medio Ambiente': 'success',
      'Social': 'warning',
      'Tecnológico': 'primary',
      'Educativo': 'info',
      'Salud': 'danger'
    };
    return classes[type] || 'secondary';
  }
}
