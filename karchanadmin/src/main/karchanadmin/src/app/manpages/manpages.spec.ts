import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Manpages } from './manpages';

describe('Manpages', () => {
  let component: Manpages;
  let fixture: ComponentFixture<Manpages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Manpages],
    }).compileComponents();

    fixture = TestBed.createComponent(Manpages);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
