import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationDashboard } from './organization-dashboard';

describe('OrganizationDashboard', () => {
  let component: OrganizationDashboard;
  let fixture: ComponentFixture<OrganizationDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrganizationDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrganizationDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
