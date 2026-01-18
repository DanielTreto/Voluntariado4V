import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './avatar.html',
  styleUrl: './avatar.css'
})
export class AvatarComponent {
  @Input() src: string = '';
  @Input() alt: string = 'Avatar';
  @Input() size: number = 40;
  @Input() border: boolean = false;
}
