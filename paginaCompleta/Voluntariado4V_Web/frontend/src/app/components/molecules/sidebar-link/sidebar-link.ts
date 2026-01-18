import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar-link',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-link.html',
  styleUrl: './sidebar-link.css'
})
export class SidebarLinkComponent {
  @Input() label: string = '';
  @Input() icon: string = '';
  @Input() route: string = '';
}
