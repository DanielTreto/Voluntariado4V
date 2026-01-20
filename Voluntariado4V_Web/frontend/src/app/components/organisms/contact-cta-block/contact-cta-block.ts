import { Component, output } from '@angular/core';

@Component({
  selector: 'app-contact-cta-block',
  imports: [],
  templateUrl: './contact-cta-block.html',
  styleUrl: './contact-cta-block.scss',
})
export class ContactCtaBlock {
  onRegisterClick = output();

  openRegisterModal() {
    this.onRegisterClick.emit();
  }
}
