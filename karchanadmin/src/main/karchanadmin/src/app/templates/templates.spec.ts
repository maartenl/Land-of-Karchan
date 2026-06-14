import { ComponentFixture, TestBed } from '@angular/core/testing';

import Templates from './templates';

describe('Templates', () => {
  let component: Templates;
  let fixture: ComponentFixture<Templates>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Templates],
    }).compileComponents();

    fixture = TestBed.createComponent(Templates);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
