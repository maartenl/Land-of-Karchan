import { TestBed } from '@angular/core/testing';

import { TemplatesRestService } from './templates-rest.service';

describe('TemplatesRestService', () => {
  let service: TemplatesRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TemplatesRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
