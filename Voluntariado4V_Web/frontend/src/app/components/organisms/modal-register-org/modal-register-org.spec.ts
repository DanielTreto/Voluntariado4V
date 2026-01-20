import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalRegisterOrg } from './modal-register-org';

describe('ModalRegisterOrg', () => {
  let component: ModalRegisterOrg;
  let fixture: ComponentFixture<ModalRegisterOrg>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalRegisterOrg]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalRegisterOrg);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
