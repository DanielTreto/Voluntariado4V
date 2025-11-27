import { Routes } from '@angular/router';

import { VolunteerTableComponent } from '../.././components/organisms/volunteer-table/volunteer-table';
import { EventCalendarComponent } from '../.././components/organisms/event-calendar/event-calendar';
import { OrganizationListComponent } from '../.././components/organisms/organization-list/organization-list';
import { ActivityListComponent } from '../.././components/organisms/activity-list/activity-list';
import { ReportsComponent } from '../.././components/organisms/reports/reports';
import { SettingsComponent } from '../.././pages/settings/settings';

export const DASHBOARD_CHILD_ROUTES: Routes = [
    { path: '', redirectTo: 'volunteers', pathMatch: 'full' },
    { path: 'volunteers', component: VolunteerTableComponent },
    { path: 'activities', component: ActivityListComponent },
    { path: 'events', component: EventCalendarComponent },
    { path: 'organizations', component: OrganizationListComponent },
    { path: 'reports', component: ReportsComponent },
    { path: 'settings', component: SettingsComponent },
    { path: '**', redirectTo: 'volunteers' }
];
