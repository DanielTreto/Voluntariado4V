import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventCtaBlock } from './event-cta-block';

describe('EventCtaBlock', () => {
  let component: EventCtaBlock;
  let fixture: ComponentFixture<EventCtaBlock>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventCtaBlock]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventCtaBlock);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
