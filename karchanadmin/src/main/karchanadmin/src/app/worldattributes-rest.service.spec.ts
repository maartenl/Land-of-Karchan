import { TestBed } from '@angular/core/testing';

import { WorldattributesRestService } from './worldattributes-rest.service';

describe('WorldattributesRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: WorldattributesRestService = TestBed.get(WorldattributesRestService);
    expect(service).toBeTruthy();
  });
});
