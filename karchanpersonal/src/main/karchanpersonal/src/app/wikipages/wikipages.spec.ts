import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Wikipages } from './wikipages';

describe('Wikipages', () => {
  let component: Wikipages;
  let fixture: ComponentFixture<Wikipages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Wikipages],
    }).compileComponents();

    fixture = TestBed.createComponent(Wikipages);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
