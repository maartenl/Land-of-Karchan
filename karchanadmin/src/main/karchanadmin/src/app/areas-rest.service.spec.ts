import { TestBed } from '@angular/core/testing';

import { AreasRestService } from './areas-rest.service';

describe('AreasRestService', () => {
  let service: AreasRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AreasRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
