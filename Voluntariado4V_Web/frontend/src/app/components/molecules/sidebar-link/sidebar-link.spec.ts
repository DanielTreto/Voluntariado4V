import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarLinkComponent } from './sidebar-link';
import { provideRouter } from '@angular/router';

describe('SidebarLinkComponent', () => {
    let component: SidebarLinkComponent;
    let fixture: ComponentFixture<SidebarLinkComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SidebarLinkComponent],
            providers: [provideRouter([])]
        })
            .compileComponents();

        fixture = TestBed.createComponent(SidebarLinkComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should accept inputs', () => {
        component.label = 'Home';
        component.icon = 'bi-house';
        component.route = '/home';
        fixture.detectChanges();

        expect(component.label).toBe('Home');
        expect(component.icon).toBe('bi-house');
        expect(component.route).toBe('/home');
    });
});
