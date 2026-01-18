import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventCalendarComponent } from './event-calendar';
import { provideRouter } from '@angular/router';

describe('EventCalendarComponent', () => {
    let component: EventCalendarComponent;
    let fixture: ComponentFixture<EventCalendarComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [EventCalendarComponent],
            providers: [provideRouter([])]
        })
            .compileComponents();

        fixture = TestBed.createComponent(EventCalendarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should return events for a specific day', () => {
        // Assuming day 5 has an event as per component logic
        const events = component.getEventsForDay(5);
        expect(events.length).toBeGreaterThan(0);
        expect(events[0].title).toBe('Limpieza de Playa');
    });

    it('should return empty array for day with no events', () => {
        const events = component.getEventsForDay(1);
        expect(events).toEqual([]);
    });
});
