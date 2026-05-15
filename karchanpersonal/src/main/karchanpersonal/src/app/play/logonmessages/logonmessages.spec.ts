import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Logonmessages } from './logonmessages';

describe('Logonmessages', () => {
  let component: Logonmessages;
  let fixture: ComponentFixture<Logonmessages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Logonmessages],
    }).compileComponents();

    fixture = TestBed.createComponent(Logonmessages);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
