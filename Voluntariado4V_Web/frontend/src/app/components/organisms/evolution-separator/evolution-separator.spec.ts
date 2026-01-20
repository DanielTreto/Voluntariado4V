import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvolutionSeparator } from './evolution-separator';

describe('EvolutionSeparator', () => {
  let component: EvolutionSeparator;
  let fixture: ComponentFixture<EvolutionSeparator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvolutionSeparator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EvolutionSeparator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
