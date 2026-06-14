import { TestBed } from '@angular/core/testing';

import { MethodsRestService } from './methods-rest.service';

describe('MethodsRestService', () => {
  let service: MethodsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MethodsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
