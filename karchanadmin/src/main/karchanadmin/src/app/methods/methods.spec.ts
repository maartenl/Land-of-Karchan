import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Methods } from './methods';

describe('Methods', () => {
  let component: Methods;
  let fixture: ComponentFixture<Methods>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Methods],
    }).compileComponents();

    fixture = TestBed.createComponent(Methods);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
