import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Systemlogs } from './systemlogs';

describe('Systemlogs', () => {
  let component: Systemlogs;
  let fixture: ComponentFixture<Systemlogs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Systemlogs],
    }).compileComponents();

    fixture = TestBed.createComponent(Systemlogs);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
