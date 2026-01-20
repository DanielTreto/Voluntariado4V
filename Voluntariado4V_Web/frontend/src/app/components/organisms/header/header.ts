import { Component, output } from '@angular/core';


@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  onLoginClick = output(); 
  onRegisterClick = output();

  // Este método emite un evento que el AppComponent captura
  openLogin(): void {
    this.onLoginClick.emit();
  }

  openRegister():void{
    this.onRegisterClick.emit();
  }
}
