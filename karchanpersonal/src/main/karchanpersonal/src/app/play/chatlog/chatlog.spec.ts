import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Chatlog } from './chatlog';

describe('Chatlog', () => {
  let component: Chatlog;
  let fixture: ComponentFixture<Chatlog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Chatlog],
    }).compileComponents();

    fixture = TestBed.createComponent(Chatlog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
