import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './badge.html',
  styleUrl: './badge.css'
})
export class BadgeComponent {
  @Input() label: string = '';
  @Input() type: 'active' | 'pending' | 'inactive' | 'org-pending' | 'suspended' | 'custom' = 'active';
  @Input() customClass: string = '';

  get badgeClass(): string {
    if (this.type === 'custom') return this.customClass;
    return `badge-${this.type}`;
  }
}
