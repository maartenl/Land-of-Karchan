import { TestBed } from '@angular/core/testing';

import { EventsRestService } from './events-rest.service';

describe('EventsRestService', () => {
  let service: EventsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
