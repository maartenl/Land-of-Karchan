import { TestBed } from '@angular/core/testing';

import { BannedipsRestService } from './bannedips-rest.service';

describe('BannedipsRestService', () => {
  let service: BannedipsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BannedipsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
