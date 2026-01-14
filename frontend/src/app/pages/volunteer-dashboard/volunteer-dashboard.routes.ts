import { Routes } from '@angular/router';
import { VolunteerActivitiesComponent } from '../../components/organisms/volunteer-activities/volunteer-activities';
import { EventCalendarComponent } from '../../components/organisms/event-calendar/event-calendar';
import { SettingsComponent } from '../../pages/settings/settings';

export const VOLUNTEER_DASHBOARD_ROUTES: Routes = [
    { path: '', redirectTo: 'activities', pathMatch: 'full' },
    { path: 'activities', component: VolunteerActivitiesComponent },
    { path: 'events', component: EventCalendarComponent },
    { path: 'settings', component: SettingsComponent },
    { path: '**', redirectTo: 'activities' }
];
