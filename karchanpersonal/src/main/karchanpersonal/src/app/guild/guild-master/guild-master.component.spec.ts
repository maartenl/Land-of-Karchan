import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GuildMasterComponent } from './guild-master.component';

describe('GuildMasterComponent', () => {
  let component: GuildMasterComponent;
  let fixture: ComponentFixture<GuildMasterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GuildMasterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GuildMasterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
