import { TestBed } from '@angular/core/testing';

import { ItemsRestService } from './items-rest.service';

describe('ItemsRestService', () => {
  let service: ItemsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
