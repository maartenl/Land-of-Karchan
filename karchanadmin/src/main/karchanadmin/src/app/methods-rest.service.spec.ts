import { TestBed } from '@angular/core/testing';

import { MethodsRestService } from './methods-rest.service';

describe('MethodsRestService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: MethodsRestService = TestBed.get(MethodsRestService);
    expect(service).toBeTruthy();
  });
});
