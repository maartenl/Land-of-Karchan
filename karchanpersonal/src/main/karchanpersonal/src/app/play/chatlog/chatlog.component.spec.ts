import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatlogComponent } from './chatlog.component';

describe('ChatlogComponent', () => {
  let component: ChatlogComponent;
  let fixture: ComponentFixture<ChatlogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChatlogComponent]
    });
    fixture = TestBed.createComponent(ChatlogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
