import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../../atoms/avatar/avatar';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, AvatarComponent],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css'
})
export class UserProfileComponent {
  @Input() name: string = '';
  @Input() role: string = '';
  @Input() avatarSrc: string = '';
}
