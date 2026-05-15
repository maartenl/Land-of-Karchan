import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerSettings } from './player-settings';

describe('PlayerSettings', () => {
  let component: PlayerSettings;
  let fixture: ComponentFixture<PlayerSettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerSettings],
    }).compileComponents();

    fixture = TestBed.createComponent(PlayerSettings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
