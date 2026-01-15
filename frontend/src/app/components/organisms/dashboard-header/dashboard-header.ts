import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-header.html',
  styleUrls: ['./dashboard-header.scss']
})
export class DashboardHeaderComponent implements OnInit {
  userName: string = 'Usuario';
  userRole: string = 'Voluntario';
  avatarSrc: string = 'assets/images/default-avatar.png'; // Default

  private router = inject(Router);

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.name) {
      this.userName = user.name;
      // Translate role for display
      this.userRole = user.role === 'organization' ? 'Organización' : 'Voluntario';

      // Set avatar (mock logic for now, or match Sidebar's previous asset)
      this.avatarSrc = user.avatar || 'assets/images/admin-avatar.png';
    }
  }

  goHome() {
    this.router.navigate(['/']);
  }

  logout() {
    localStorage.removeItem('user');
    this.router.navigate(['/']);
  }
}
