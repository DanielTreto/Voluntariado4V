import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-header.html',
  styleUrls: ['./dashboard-header.scss']
})
export class DashboardHeaderComponent implements OnInit {
  @Input() title: string = 'Volunteer Management'; // Default title

  userName: string = 'Usuario';
  userRole: string = 'Voluntario';
  avatarSrc: string = 'assets/images/default-avatar.png'; // Default

  private router = inject(Router);
  private apiService = inject(ApiService);

  ngOnInit(): void {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.name) {
      this.userName = user.name;
      // Translate role for display
      this.userRole = user.role === 'organization' ? 'Organización' : 'Voluntario';

      // Set avatar
      if (user.avatar) {
        if (user.avatar.startsWith('/uploads/')) {
          this.avatarSrc = this.apiService.baseUrl + user.avatar;
        } else {
          this.avatarSrc = user.avatar;
        }
      } else {
        this.avatarSrc = 'assets/images/default-avatar.png';
      }
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
