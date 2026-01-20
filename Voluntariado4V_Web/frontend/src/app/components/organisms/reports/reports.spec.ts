import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReportsComponent } from './reports';

// Mock Chart.js to avoid canvas errors
declare global {
    interface Window {
        Chart: any;
    }
}

describe('ReportsComponent', () => {
    let component: ReportsComponent;
    let fixture: ComponentFixture<ReportsComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ReportsComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(ReportsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize charts after view init', () => {
        spyOn(component, 'initCharts');
        component.ngAfterViewInit();
        expect(component.initCharts).toHaveBeenCalled();
    });
});
