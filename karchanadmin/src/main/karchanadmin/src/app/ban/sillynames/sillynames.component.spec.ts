import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SillynamesComponent } from './sillynames.component';

describe('SillynamesComponent', () => {
  let component: SillynamesComponent;
  let fixture: ComponentFixture<SillynamesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SillynamesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SillynamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
