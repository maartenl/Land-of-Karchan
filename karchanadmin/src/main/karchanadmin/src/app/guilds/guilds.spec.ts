import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Guilds } from './guilds';

describe('Guilds', () => {
  let component: Guilds;
  let fixture: ComponentFixture<Guilds>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Guilds],
    }).compileComponents();

    fixture = TestBed.createComponent(Guilds);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
