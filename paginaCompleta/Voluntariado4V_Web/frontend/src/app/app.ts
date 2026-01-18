import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService } from './services/api.service';


import { ScrollToTopComponent } from './components/atoms/scroll-to-top/scroll-to-top';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ScrollToTopComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  protected readonly title = signal('Voluntariado 4V');
  protected usuarios = signal<any[]>([]);
  private apiService = inject(ApiService);

  ngOnInit() {
    this.apiService.getUsuarios().subscribe({
      next: (data) => {
        // console.log('Datos recibidos del backend:', data);
        this.usuarios.set(data);
      },
      error: (err) => console.error('Error al conectar con el backend:', err)
    });
  }
}
