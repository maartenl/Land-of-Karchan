import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxMail } from './inbox-mail';

describe('InboxMail', () => {
  let component: InboxMail;
  let fixture: ComponentFixture<InboxMail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InboxMail],
    }).compileComponents();

    fixture = TestBed.createComponent(InboxMail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
