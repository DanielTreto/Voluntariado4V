import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
  styles: [`
    :host {
        display: block;
        width: 100%;
    }
  `]
})
export class Footer {
  @Input() showCta: boolean = true;
}
