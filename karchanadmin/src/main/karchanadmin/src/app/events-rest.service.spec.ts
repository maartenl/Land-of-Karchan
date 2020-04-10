import { TestBed } from '@angular/core/testing';

import { EventsRestService } from './events-rest.service';

describe('EventsRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: EventsRestService = TestBed.get(EventsRestService);
    expect(service).toBeTruthy();
  });
});
