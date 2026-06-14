import { TestBed } from '@angular/core/testing';

import { WorldattributesRestService } from './worldattributes-rest.service';

describe('WorldattributesRestService', () => {
  let service: WorldattributesRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorldattributesRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
