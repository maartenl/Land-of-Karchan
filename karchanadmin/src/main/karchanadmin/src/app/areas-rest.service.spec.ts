import { TestBed } from '@angular/core/testing';

import { AreasRestService } from './areas-rest.service';

describe('AreasRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AreasRestService = TestBed.get(AreasRestService);
    expect(service).toBeTruthy();
  });
});
