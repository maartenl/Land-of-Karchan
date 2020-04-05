import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BannednamesComponent } from './bannednames.component';

describe('BannednamesComponent', () => {
  let component: BannednamesComponent;
  let fixture: ComponentFixture<BannednamesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BannednamesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BannednamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
