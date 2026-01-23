import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalRegisterVol } from './modal-register-vol';

describe('ModalRegisterVol', () => {
  let component: ModalRegisterVol;
  let fixture: ComponentFixture<ModalRegisterVol>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalRegisterVol]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalRegisterVol);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
