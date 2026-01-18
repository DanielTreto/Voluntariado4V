import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchBarComponent } from './search-bar';

describe('SearchBarComponent', () => {
    let component: SearchBarComponent;
    let fixture: ComponentFixture<SearchBarComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [SearchBarComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(SearchBarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should have default placeholder', () => {
        expect(component.placeholder).toBe('Search...');
    });

    it('should accept custom placeholder', () => {
        component.placeholder = 'Search users...';
        fixture.detectChanges();
        expect(component.placeholder).toBe('Search users...');
    });
});
