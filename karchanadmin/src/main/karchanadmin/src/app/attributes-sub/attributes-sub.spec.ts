import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttributesSub } from './attributes-sub';

describe('AttributesSub', () => {
  let component: AttributesSub;
  let fixture: ComponentFixture<AttributesSub>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttributesSub],
    }).compileComponents();

    fixture = TestBed.createComponent(AttributesSub);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
