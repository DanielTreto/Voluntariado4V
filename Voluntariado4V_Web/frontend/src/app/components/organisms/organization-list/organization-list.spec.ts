import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrganizationListComponent } from './organization-list';

describe('OrganizationListComponent', () => {
    let component: OrganizationListComponent;
    let fixture: ComponentFixture<OrganizationListComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [OrganizationListComponent]
        })
            .compileComponents();

        fixture = TestBed.createComponent(OrganizationListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should move org from pending to registered on accept', () => {
        const pendingOrg = component.pendingOrgs[0];
        component.acceptOrg(pendingOrg);

        expect(component.pendingOrgs.find(o => o.id === pendingOrg.id)).toBeUndefined();
        expect(component.registeredOrgs.find(o => o.id === pendingOrg.id)).toBeDefined();
    });

    it('should remove org from pending on deny', () => {
        // Reset or ensure there is a pending org
        if (component.pendingOrgs.length === 0) {
            component.pendingOrgs = [{
                id: 99,
                name: 'Test Org',
                email: 'test@org.com',
                status: 'org-pending',
                logo: ''
            }];
        }
        const pendingOrg = component.pendingOrgs[0];
        component.denyOrg(pendingOrg);

        expect(component.pendingOrgs.find(o => o.id === pendingOrg.id)).toBeUndefined();
    });

    it('should suspend an active org', () => {
        const activeOrg = component.registeredOrgs[0];
        component.openSuspendConfirm(activeOrg);
        component.suspendOrg();

        expect(component.registeredOrgs.find(o => o.id === activeOrg.id)?.status).toBe('suspended');
    });
});
