import { TestBed, inject } from '@angular/core/testing';

import { ErrorsService } from './errors.service';

describe('ErrorsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ErrorsService]
    });
  });

  it('should ...', inject([ErrorsService], (service: ErrorsService) => {
    expect(service).toBeTruthy();
  }));
});
