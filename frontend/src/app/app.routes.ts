import { ROUTES } from '@angular/router'; // Remove unused routes import if needed, or keep Routes type
import { Routes } from '@angular/router';
import { PaginaPrincipal } from './pagina-principal/pagina-principal';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { DASHBOARD_CHILD_ROUTES } from './pages/dashboard/dashboard.routes';
import { VolunteerDashboardComponent } from './pages/volunteer-dashboard/volunteer-dashboard.component';
import { OrganizationDashboardComponent } from './pages/organization-dashboard/organization-dashboard.component';

export const routes: Routes = [
    { 
        path: '', 
        redirectTo: 'pagina-principal', 
        pathMatch: 'full'
    },
    { path: 'pagina-principal', component: PaginaPrincipal },
    { path: 'dashboard', component: DashboardComponent ,children: DASHBOARD_CHILD_ROUTES,},
    { path: 'volunteer-dashboard', component: VolunteerDashboardComponent },
    { path: 'organization-dashboard', component: OrganizationDashboardComponent },
];
