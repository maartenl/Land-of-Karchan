import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldattributesComponent } from './worldattributes.component';

describe('WorldattributesComponent', () => {
  let component: WorldattributesComponent;
  let fixture: ComponentFixture<WorldattributesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorldattributesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldattributesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
