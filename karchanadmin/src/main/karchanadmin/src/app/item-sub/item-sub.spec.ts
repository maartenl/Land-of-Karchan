import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemSub } from './item-sub';

describe('ItemSub', () => {
  let component: ItemSub;
  let fixture: ComponentFixture<ItemSub>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ItemSub],
    }).compileComponents();

    fixture = TestBed.createComponent(ItemSub);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
