import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Attributes } from './attributes';

describe('Attributes', () => {
  let component: Attributes;
  let fixture: ComponentFixture<Attributes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Attributes],
    }).compileComponents();

    fixture = TestBed.createComponent(Attributes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
