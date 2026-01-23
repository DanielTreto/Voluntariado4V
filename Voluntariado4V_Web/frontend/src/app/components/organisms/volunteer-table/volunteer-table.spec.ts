import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VolunteerTableComponent } from './volunteer-table';
import { ApiService } from '../../../services/api.service';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('VolunteerTableComponent', () => {
    let component: VolunteerTableComponent;
    let fixture: ComponentFixture<VolunteerTableComponent>;
    let apiServiceSpy: jasmine.SpyObj<ApiService>;

    beforeEach(async () => {
        apiServiceSpy = jasmine.createSpyObj('ApiService', ['getVolunteers']);
        apiServiceSpy.getVolunteers.and.returnValue(of([{
            id: 1,
            name: 'John',
            surname1: 'Doe',
            course: 'Physics',
            email: 'john@doe.com',
            phone: '123456789',
            status: 'PENDIENTE',
            dni: '12345678A'
        }]));

        await TestBed.configureTestingModule({
            imports: [VolunteerTableComponent, HttpClientTestingModule],
            providers: [
                { provide: ApiService, useValue: apiServiceSpy }
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(VolunteerTableComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load volunteers on init', () => {
        expect(apiServiceSpy.getVolunteers).toHaveBeenCalled();
        expect(component.volunteers.length).toBe(1);
        expect(component.volunteers[0].name).toBe('John Doe');
    });

    it('should map status correctly', () => {
        expect(component.mapStatus('PENDIENTE')).toBe('pending');
        expect(component.mapStatus('ACTIVO')).toBe('active');
        expect(component.mapStatus('UNKNOWN')).toBe('pending');
    });

    it('should accept volunteer', () => {
        const vol = component.volunteers[0];
        component.acceptVolunteer(vol);
        expect(vol.status).toBe('active');
    });

    it('should deny volunteer', () => {
        const vol = component.volunteers[0];
        component.denyVolunteer(vol);
        expect(component.volunteers.length).toBe(0);
    });
});
