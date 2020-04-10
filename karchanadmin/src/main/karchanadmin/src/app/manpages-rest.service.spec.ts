import { TestBed } from '@angular/core/testing';

import { ManpagesRestService } from './manpages-rest.service';

describe('ManpagesRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ManpagesRestService = TestBed.get(ManpagesRestService);
    expect(service).toBeTruthy();
  });
});
