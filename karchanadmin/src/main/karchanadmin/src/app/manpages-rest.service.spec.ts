import { TestBed } from '@angular/core/testing';

import { ManpagesRestService } from './manpages-rest.service';

describe('ManpagesRestService', () => {
  let service: ManpagesRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManpagesRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
