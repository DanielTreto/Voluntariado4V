import { Component, ElementRef } from '@angular/core';
import { Header } from ".././components/organisms/header/header";
import { Footer } from ".././components/organisms/footer/footer";
import { HomePage } from ".././components/organisms/home-page/home-page";
import { ModalLogin } from ".././components/organisms/modal-login/modal-login";
import { ModalRegisterVol } from ".././components/organisms/modal-register-vol/modal-register-vol";
import { ModalRegisterOrg } from ".././components/organisms/modal-register-org/modal-register-org";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pagina-principal',
  imports: [Header, Footer, HomePage, ModalLogin, ModalRegisterVol, ModalRegisterOrg,CommonModule],
  templateUrl: './pagina-principal.html',
  styleUrl: './pagina-principal.scss',
})
export class PaginaPrincipal {
    activeModalType: string | null = null; 

  constructor(private el: ElementRef) {}

  openModal(type: string): void {
    // Asigna el tipo de modal a la variable para mostrarlo en el template
    this.activeModalType = type;
    document.body.classList.add('overflow-hidden');
  }

  // Ahora el closeModal pone el estado en null
  closeModal(): void {
    this.activeModalType = null;
    document.body.classList.remove('overflow-hidden');
  }

  // Método para manejar la navegación de vuelta desde el modal de registro
  openLoginFromRegister(): void {
    this.activeModalType = 'login';
  }

  // Cierra el modal si se hace clic en el overlay (fondo negro)
  closeOnOverlayClick(event: MouseEvent): void {
    const overlayElement = this.el.nativeElement.querySelector('.modal-overlay-container');
    if (event.target === overlayElement) {
      this.closeModal();
    }
  }
}
