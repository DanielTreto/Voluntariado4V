import { Component, output } from '@angular/core';
import { HeroSection } from "../hero-section/hero-section";
import { EvolutionSeparator } from "../evolution-separator/evolution-separator";
import { ImpactGrid } from "../impact-grid/impact-grid";
import { EventCtaBlock } from "../event-cta-block/event-cta-block";
import { ContactCtaBlock } from "../contact-cta-block/contact-cta-block";

@Component({
  selector: 'app-home-page',
  imports: [HeroSection, EvolutionSeparator, ImpactGrid, EventCtaBlock, ContactCtaBlock],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage {
  onOpenRegister = output();

  handleRegisterClick() {
    this.onOpenRegister.emit();
  }
}
