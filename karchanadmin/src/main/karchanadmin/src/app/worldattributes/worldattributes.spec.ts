import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Worldattributes } from './worldattributes';

describe('Worldattributes', () => {
  let component: Worldattributes;
  let fixture: ComponentFixture<Worldattributes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Worldattributes],
    }).compileComponents();

    fixture = TestBed.createComponent(Worldattributes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
