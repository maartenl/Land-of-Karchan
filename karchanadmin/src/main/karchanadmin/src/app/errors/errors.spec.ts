import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Errors } from './errors';

describe('Errors', () => {
  let component: Errors;
  let fixture: ComponentFixture<Errors>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Errors],
    }).compileComponents();

    fixture = TestBed.createComponent(Errors);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
