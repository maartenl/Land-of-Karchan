import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Unbanned } from './unbanned';

describe('Unbanned', () => {
  let component: Unbanned;
  let fixture: ComponentFixture<Unbanned>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Unbanned],
    }).compileComponents();

    fixture = TestBed.createComponent(Unbanned);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
