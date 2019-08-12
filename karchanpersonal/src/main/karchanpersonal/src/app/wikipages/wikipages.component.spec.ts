import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WikipagesComponent } from './wikipages.component';

describe('WikipagesComponent', () => {
  let component: WikipagesComponent;
  let fixture: ComponentFixture<WikipagesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WikipagesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WikipagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
