import { TestBed } from '@angular/core/testing';

import { CommandsRestService } from './commands-rest.service';

describe('CommandsRestService', () => {
  let service: CommandsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CommandsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
