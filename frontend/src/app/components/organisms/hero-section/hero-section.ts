import { Component, output } from '@angular/core';

@Component({
  selector: 'app-hero-section',
  imports: [],
  templateUrl: './hero-section.html',
  styleUrl: './hero-section.scss',
})
export class HeroSection {
  onRegisterClick = output();

  openRegisterModal() {
    this.onRegisterClick.emit();
  }
}
