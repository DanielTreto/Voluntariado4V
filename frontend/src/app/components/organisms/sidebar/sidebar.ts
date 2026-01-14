import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarLinkComponent } from '../../molecules/sidebar-link/sidebar-link';
import { UserProfileComponent } from '../../molecules/user-profile/user-profile';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, SidebarLinkComponent, UserProfileComponent],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class SidebarComponent {
  @Input() links: { label: string, icon: string, route: string }[] = [];
}
