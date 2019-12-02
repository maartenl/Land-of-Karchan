import { TestBed } from '@angular/core/testing';

import { CommandsRestService } from './commands-rest.service';

describe('CommandsRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CommandsRestService = TestBed.get(CommandsRestService);
    expect(service).toBeTruthy();
  });
});
