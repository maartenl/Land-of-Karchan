import { TestBed } from '@angular/core/testing';

import { BlogsRestService } from './blogs-rest.service';

describe('BlogsRestService', () => {
  let service: BlogsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BlogsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
