import { TestBed } from '@angular/core/testing';

import { BoardsRestService } from './boards-rest.service';

describe('BoardsRestService', () => {
  let service: BoardsRestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BoardsRestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
