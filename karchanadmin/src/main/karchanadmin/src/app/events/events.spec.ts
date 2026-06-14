import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Events } from './events';

describe('Events', () => {
  let component: Events;
  let fixture: ComponentFixture<Events>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Events],
    }).compileComponents();

    fixture = TestBed.createComponent(Events);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
