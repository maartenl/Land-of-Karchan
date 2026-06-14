import { TestBed } from '@angular/core/testing';

import { GuildsRestService } from './guilds-rest.service';

describe('GuildsRestService', () => {
  let service: GuildsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GuildsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
