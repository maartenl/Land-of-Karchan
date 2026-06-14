import { TestBed } from '@angular/core/testing';

import { SystemlogsRestService } from './systemlogs-rest.service';

describe('SystemlogsRestService', () => {
  let service: SystemlogsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SystemlogsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
