import { TestBed } from '@angular/core/testing';

import { AdministratorsRestService } from './administrators-rest.service';

describe('AdministratorsRestService', () => {
  let service: AdministratorsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdministratorsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
