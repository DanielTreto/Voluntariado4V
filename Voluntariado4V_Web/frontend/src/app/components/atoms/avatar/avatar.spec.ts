import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvatarComponent } from './avatar';

describe('AvatarComponent', () => {
    let component: AvatarComponent;
    let fixture: ComponentFixture<AvatarComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [AvatarComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(AvatarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should have default input values', () => {
        expect(component.src).toBe('');
        expect(component.alt).toBe('Avatar');
        expect(component.size).toBe(40);
        expect(component.border).toBeFalse();
    });

    it('should accept custom input values', () => {
        component.src = 'test-image.jpg';
        component.alt = 'Test Avatar';
        component.size = 50;
        component.border = true;
        fixture.detectChanges();

        expect(component.src).toBe('test-image.jpg');
        expect(component.alt).toBe('Test Avatar');
        expect(component.size).toBe(50);
        expect(component.border).toBeTrue();
    });
});
