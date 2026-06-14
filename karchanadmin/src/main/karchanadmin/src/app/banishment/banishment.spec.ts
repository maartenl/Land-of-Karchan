import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Banishment } from './banishment';

describe('Banishment', () => {
  let component: Banishment;
  let fixture: ComponentFixture<Banishment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Banishment],
    }).compileComponents();

    fixture = TestBed.createComponent(Banishment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
