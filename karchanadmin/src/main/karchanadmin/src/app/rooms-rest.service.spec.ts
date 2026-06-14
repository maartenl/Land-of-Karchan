import { TestBed } from '@angular/core/testing';

import { RoomsRestService } from './rooms-rest.service';

describe('RoomsRestService', () => {
  let service: RoomsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoomsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
