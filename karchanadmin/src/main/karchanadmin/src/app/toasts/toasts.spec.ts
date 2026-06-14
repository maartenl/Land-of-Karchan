import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Toasts } from './toasts';

describe('Toasts', () => {
  let component: Toasts;
  let fixture: ComponentFixture<Toasts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Toasts],
    }).compileComponents();

    fixture = TestBed.createComponent(Toasts);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
