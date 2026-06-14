import { TestBed } from '@angular/core/testing';

import { CharactersRestService } from './characters-rest.service';

describe('CharactersRestService', () => {
  let service: CharactersRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CharactersRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
