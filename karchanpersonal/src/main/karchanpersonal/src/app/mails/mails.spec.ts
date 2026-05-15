import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Mails } from './mails';

describe('Mails', () => {
  let component: Mails;
  let fixture: ComponentFixture<Mails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Mails],
    }).compileComponents();

    fixture = TestBed.createComponent(Mails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
