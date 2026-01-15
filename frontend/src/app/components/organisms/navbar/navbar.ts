import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchBarComponent } from '../../molecules/search-bar/search-bar';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, SearchBarComponent],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  @Input() title: string = 'Dashboard Admin';
}
