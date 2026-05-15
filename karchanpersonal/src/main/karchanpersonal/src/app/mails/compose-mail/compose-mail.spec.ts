import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComposeMail } from './compose-mail';

describe('ComposeMail', () => {
  let component: ComposeMail;
  let fixture: ComponentFixture<ComposeMail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComposeMail],
    }).compileComponents();

    fixture = TestBed.createComponent(ComposeMail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
