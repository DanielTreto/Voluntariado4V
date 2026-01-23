import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserProfileComponent } from './user-profile';

describe('UserProfileComponent', () => {
    let component: UserProfileComponent;
    let fixture: ComponentFixture<UserProfileComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [UserProfileComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(UserProfileComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should accept inputs', () => {
        component.name = 'John Doe';
        component.role = 'Admin';
        component.avatarSrc = 'avatar.jpg';
        fixture.detectChanges();

        expect(component.name).toBe('John Doe');
        expect(component.role).toBe('Admin');
        expect(component.avatarSrc).toBe('avatar.jpg');
    });
});
