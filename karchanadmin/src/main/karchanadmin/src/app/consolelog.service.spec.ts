import { TestBed } from '@angular/core/testing';

import { ConsolelogService } from './consolelog.service';

describe('ConsolelogService', () => {
  let service: ConsolelogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConsolelogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
