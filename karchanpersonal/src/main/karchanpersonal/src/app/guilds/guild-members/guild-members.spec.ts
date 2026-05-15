import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuildMembers } from './guild-members';

describe('GuildMember', () => {
  let component: GuildMembers;
  let fixture: ComponentFixture<GuildMembers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuildMembers],
    }).compileComponents();

    fixture = TestBed.createComponent(GuildMembers);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
