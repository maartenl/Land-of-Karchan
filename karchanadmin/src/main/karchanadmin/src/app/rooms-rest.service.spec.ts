import { TestBed } from '@angular/core/testing';

import { RoomsRestService } from './rooms-rest.service';

describe('RoomsRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RoomsRestService = TestBed.get(RoomsRestService);
    expect(service).toBeTruthy();
  });
});
