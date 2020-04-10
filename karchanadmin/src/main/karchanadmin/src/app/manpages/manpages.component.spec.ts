import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManpagesComponent } from './manpages.component';

describe('ManpagesComponent', () => {
  let component: ManpagesComponent;
  let fixture: ComponentFixture<ManpagesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManpagesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManpagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
