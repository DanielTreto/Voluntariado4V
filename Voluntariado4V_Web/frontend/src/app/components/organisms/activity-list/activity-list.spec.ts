import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivityListComponent } from './activity-list';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AvatarComponent } from '../../atoms/avatar/avatar';
import { BadgeComponent } from '../../atoms/badge/badge';

describe('ActivityListComponent', () => {
    let component: ActivityListComponent;
    let fixture: ComponentFixture<ActivityListComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ActivityListComponent, CommonModule, AvatarComponent, BadgeComponent, FormsModule]
        })
            .compileComponents();

        fixture = TestBed.createComponent(ActivityListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should filter activities by active tab', () => {
        component.activeTab = 'active';
        expect(component.filteredActivities.every(a => a.status === 'active')).toBeTrue();

        component.activeTab = 'pending';
        expect(component.filteredActivities.every(a => a.status === 'pending')).toBeTrue();
    });

    it('should create a new activity', () => {
        const initialCount = component.activities.length;
        component.newActivity = {
            title: 'New Activity',
            description: 'Desc',
            location: 'Loc',
            date: '2025-01-01',
            type: 'Social',
            status: 'active'
        };
        component.createActivity();

        expect(component.activities.length).toBe(initialCount + 1);
        const added = component.activities[component.activities.length - 1];
        expect(added.title).toBe('New Activity');
    });

    it('should delete an activity', () => {
        const activity = component.activities[0];
        component.openDeleteConfirm(activity);
        component.deleteActivity();

        expect(component.activities.find(a => a.id === activity.id)).toBeUndefined();
    });
});
