import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SentMail } from './sent-mail';

describe('SentMail', () => {
  let component: SentMail;
  let fixture: ComponentFixture<SentMail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SentMail],
    }).compileComponents();

    fixture = TestBed.createComponent(SentMail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
