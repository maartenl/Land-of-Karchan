import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GuildsComponent } from './guilds.component';

describe('GuildsComponent', () => {
  let component: GuildsComponent;
  let fixture: ComponentFixture<GuildsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GuildsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GuildsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
