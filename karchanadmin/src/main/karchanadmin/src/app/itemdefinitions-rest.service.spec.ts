import { TestBed } from '@angular/core/testing';

import { ItemdefinitionsRestService } from './itemdefinitions-rest.service';

describe('ItemdefinitionsRestService', () => {
  let service: ItemdefinitionsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItemdefinitionsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
