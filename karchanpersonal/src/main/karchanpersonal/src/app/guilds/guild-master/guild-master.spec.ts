import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuildMaster } from './guild-master';

describe('GuildMaster', () => {
  let component: GuildMaster;
  let fixture: ComponentFixture<GuildMaster>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuildMaster],
    }).compileComponents();

    fixture = TestBed.createComponent(GuildMaster);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
