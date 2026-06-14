import { TestBed } from '@angular/core/testing';

import { BannednamesRestService } from './bannednames-rest.service';

describe('BannednamesRestService', () => {
  let service: BannednamesRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BannednamesRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
