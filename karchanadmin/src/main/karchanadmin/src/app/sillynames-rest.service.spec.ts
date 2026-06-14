import { TestBed } from '@angular/core/testing';

import { SillynamesRestService } from './sillynames-rest.service';

describe('SillynamesRestService', () => {
  let service: SillynamesRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SillynamesRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
