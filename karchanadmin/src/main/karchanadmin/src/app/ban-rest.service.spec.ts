import { TestBed } from '@angular/core/testing';

import { BanRestService } from './ban-rest.service';

describe('BanRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BanRestService = TestBed.get(BanRestService);
    expect(service).toBeTruthy();
  });
});
