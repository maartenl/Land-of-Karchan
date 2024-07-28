import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemSubComponent } from './item-sub.component';

describe('ItemSubComponent', () => {
  let component: ItemSubComponent;
  let fixture: ComponentFixture<ItemSubComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemSubComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ItemSubComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
