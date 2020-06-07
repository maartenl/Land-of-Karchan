import { TestBed } from '@angular/core/testing';

import { ChatlogService } from './chatlog.service';

describe('ChatlogService', () => {
  let service: ChatlogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChatlogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
