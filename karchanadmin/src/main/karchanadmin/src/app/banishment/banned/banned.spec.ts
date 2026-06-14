import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Banned } from './banned';

describe('Banned', () => {
  let component: Banned;
  let fixture: ComponentFixture<Banned>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Banned],
    }).compileComponents();

    fixture = TestBed.createComponent(Banned);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
