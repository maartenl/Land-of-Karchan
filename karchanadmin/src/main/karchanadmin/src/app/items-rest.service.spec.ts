import { TestBed } from '@angular/core/testing';

import { ItemsRestService } from './items-rest.service';

describe('ItemsRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ItemsRestService = TestBed.get(ItemsRestService);
    expect(service).toBeTruthy();
  });
});
