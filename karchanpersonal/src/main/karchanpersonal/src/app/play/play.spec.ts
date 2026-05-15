import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Play } from './play';

describe('Play', () => {
  let component: Play;
  let fixture: ComponentFixture<Play>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Play],
    }).compileComponents();

    fixture = TestBed.createComponent(Play);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
