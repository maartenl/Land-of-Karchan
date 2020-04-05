import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnbannedComponent } from './unbanned.component';

describe('UnbannedComponent', () => {
  let component: UnbannedComponent;
  let fixture: ComponentFixture<UnbannedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnbannedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnbannedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
