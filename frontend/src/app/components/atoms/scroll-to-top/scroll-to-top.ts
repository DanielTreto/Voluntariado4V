import { Component, HostListener, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';

@Component({
    selector: 'app-scroll-to-top',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './scroll-to-top.html',
    styleUrls: ['./scroll-to-top.css']
})
export class ScrollToTopComponent {
    showButton = false;
    isLaunching = false;

    constructor(@Inject(PLATFORM_ID) private platformId: Object) { }

    @HostListener('window:scroll', [])
    onWindowScroll() {
        if (isPlatformBrowser(this.platformId) && !this.isLaunching) { // Hide logic if launching
            if (window.scrollY > 300) {
                this.showButton = true;
            } else {
                this.showButton = false;
            }
        }
    }

    scrollToTop() {
        if (isPlatformBrowser(this.platformId)) {
            this.isLaunching = true;

            // Calculate duration needed? Or just smooth scroll
            window.scrollTo({ top: 0, behavior: 'smooth' });

            // Reset after animation
            setTimeout(() => {
                this.isLaunching = false;
                this.showButton = false; // Hide until scrolled down again
            }, 1000);
        }
    }
}
