import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Sillynames } from './sillynames';

describe('Sillynames', () => {
  let component: Sillynames;
  let fixture: ComponentFixture<Sillynames>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Sillynames],
    }).compileComponents();

    fixture = TestBed.createComponent(Sillynames);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
