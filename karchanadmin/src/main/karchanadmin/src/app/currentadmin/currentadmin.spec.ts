import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Currentadmin } from './currentadmin';

describe('Currentadmin', () => {
  let component: Currentadmin;
  let fixture: ComponentFixture<Currentadmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Currentadmin],
    }).compileComponents();

    fixture = TestBed.createComponent(Currentadmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
