import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactCtaBlock } from './contact-cta-block';

describe('ContactCtaBlock', () => {
  let component: ContactCtaBlock;
  let fixture: ComponentFixture<ContactCtaBlock>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContactCtaBlock]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContactCtaBlock);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
