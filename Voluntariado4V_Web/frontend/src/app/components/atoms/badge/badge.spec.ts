import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BadgeComponent } from './badge';

describe('BadgeComponent', () => {
    let component: BadgeComponent;
    let fixture: ComponentFixture<BadgeComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [BadgeComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(BadgeComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should have default input values', () => {
        expect(component.label).toBe('');
        expect(component.type).toBe('active');
        expect(component.customClass).toBe('');
    });

    it('should return correct badge class for predefined types', () => {
        component.type = 'pending';
        expect(component.badgeClass).toBe('badge-pending');

        component.type = 'inactive';
        expect(component.badgeClass).toBe('badge-inactive');
    });

    it('should return custom class when type is custom', () => {
        component.type = 'custom';
        component.customClass = 'my-custom-class';
        expect(component.badgeClass).toBe('my-custom-class');
    });
});
