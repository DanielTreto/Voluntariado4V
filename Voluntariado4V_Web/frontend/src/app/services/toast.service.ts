import { Injectable, signal } from '@angular/core';

export interface Toast {
    message: string;
    type: 'success' | 'error' | 'info' | 'warning';
    id: number;
}

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    toasts = signal<Toast[]>([]);

    show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info') {
        const id = Date.now();
        this.toasts.update(toasts => [...toasts, { message, type, id }]);
        setTimeout(() => this.remove(id), 3000);
    }

    remove(id: number) {
        this.toasts.update(toasts => toasts.filter(t => t.id !== id));
    }
}
