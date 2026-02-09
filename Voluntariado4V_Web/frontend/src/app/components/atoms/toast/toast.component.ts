import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../services/toast.service';

@Component({
    selector: 'app-toast',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './toast.component.html',
    styles: [`
    .toast-container { z-index: 1055; }
  `]
})
export class ToastComponent {
    toastService = inject(ToastService);
}
