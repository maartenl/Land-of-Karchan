import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Bannednames } from './bannednames';

describe('Bannednames', () => {
  let component: Bannednames;
  let fixture: ComponentFixture<Bannednames>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bannednames],
    }).compileComponents();

    fixture = TestBed.createComponent(Bannednames);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
