import { TestBed } from '@angular/core/testing';

import { UnbannedRestService } from './unbanned-rest.service';

describe('UnbannedRestService', () => {
  let service: UnbannedRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UnbannedRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
