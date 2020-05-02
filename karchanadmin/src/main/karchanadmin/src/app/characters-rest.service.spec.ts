import { TestBed } from '@angular/core/testing';

import { CharactersRestService } from './characters-rest.service';

describe('CharactersRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CharactersRestService = TestBed.get(CharactersRestService);
    expect(service).toBeTruthy();
  });
});
