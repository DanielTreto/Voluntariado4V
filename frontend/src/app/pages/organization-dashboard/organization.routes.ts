import { Routes } from '@angular/router';
import { OrgActivitiesComponent } from './org-activities/org-activities';
import { OrgSettingsComponent } from './org-settings/org-settings';
import { EventCalendarComponent } from '../../components/organisms/event-calendar/event-calendar';

export const ORGANIZATION_DASHBOARD_ROUTES: Routes = [
    { path: '', redirectTo: 'activities', pathMatch: 'full' },
    { path: 'activities', component: OrgActivitiesComponent },
    { path: 'events', component: EventCalendarComponent }, // Will need to handle passing OrgId via component inputs or modifying component to read from logged user if not passed as Input. 
    // Actually, EventCalendar expects Input. Router components can't easily receive Inputs from Router.
    // Solution: Create a wrapper 'OrgEventsPage' or update EventCalendar to check user role/id from storage if no input provided.
    // OR: Use BindToComponentInputs logic in Angular.
    { path: 'settings', component: OrgSettingsComponent }
];
