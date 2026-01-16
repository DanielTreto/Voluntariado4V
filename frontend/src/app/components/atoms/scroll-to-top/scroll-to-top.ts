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

            window.scrollTo({ top: 0, behavior: 'smooth' });

            // Keep launching state until likely finished or scrolled up
            // We can rely on onWindowScroll to hide the button when scrollY < 300
            // But we prevent toggle during launch to avoid flickering

            const checkScroll = setInterval(() => {
                if (window.scrollY === 0) {
                    this.isLaunching = false;
                    this.showButton = false;
                    clearInterval(checkScroll);
                }
            }, 100);

            // Fallback
            setTimeout(() => {
                if (this.isLaunching) {
                    this.isLaunching = false;
                    clearInterval(checkScroll);
                }
            }, 2000);
        }
    }
}
