import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Pictures } from './pictures';

describe('Pictures', () => {
  let component: Pictures;
  let fixture: ComponentFixture<Pictures>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Pictures],
    }).compileComponents();

    fixture = TestBed.createComponent(Pictures);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
