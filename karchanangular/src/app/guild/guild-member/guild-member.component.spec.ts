import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GuildMemberComponent } from './guild-member.component';

describe('GuildMemberComponent', () => {
  let component: GuildMemberComponent;
  let fixture: ComponentFixture<GuildMemberComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GuildMemberComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GuildMemberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
