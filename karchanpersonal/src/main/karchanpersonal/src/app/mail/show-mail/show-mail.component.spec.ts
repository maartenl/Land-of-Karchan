import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowMailComponent } from './show-mail.component';

describe('ShowMailComponent', () => {
  let component: ShowMailComponent;
  let fixture: ComponentFixture<ShowMailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowMailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowMailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
