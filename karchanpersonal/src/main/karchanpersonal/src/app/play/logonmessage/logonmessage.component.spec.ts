import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LogonmessageComponent } from './logonmessage.component';

describe('LogonmessageComponent', () => {
  let component: LogonmessageComponent;
  let fixture: ComponentFixture<LogonmessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LogonmessageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogonmessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
