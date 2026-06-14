import { TestBed } from '@angular/core/testing';

import { AttributesRestService } from './attributes-rest.service';

describe('AttributesRestService', () => {
  let service: AttributesRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AttributesRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
