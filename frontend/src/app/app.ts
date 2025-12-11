import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ApiService } from './services/api.service';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
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
        console.log('Datos recibidos del backend:', data);
        this.usuarios.set(data);
      },
      error: (err) => console.error('Error al conectar con el backend:', err)
    });
  }
}
