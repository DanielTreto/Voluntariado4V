import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpactGrid } from './impact-grid';

describe('ImpactGrid', () => {
  let component: ImpactGrid;
  let fixture: ComponentFixture<ImpactGrid>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImpactGrid]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImpactGrid);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
