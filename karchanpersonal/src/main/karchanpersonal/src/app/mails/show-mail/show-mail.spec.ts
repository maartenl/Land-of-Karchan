import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowMail } from './show-mail';

describe('ShowMail', () => {
  let component: ShowMail;
  let fixture: ComponentFixture<ShowMail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowMail],
    }).compileComponents();

    fixture = TestBed.createComponent(ShowMail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
