import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Blogs } from './blogs';

describe('Blogs', () => {
  let component: Blogs;
  let fixture: ComponentFixture<Blogs>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Blogs],
    }).compileComponents();

    fixture = TestBed.createComponent(Blogs);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
