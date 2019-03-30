import { Injectable } from '@angular/core';

import { ErrorMessage } from './errors/errormessage.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {
  private listener: (error: ErrorMessage) => void;

  constructor() { }

  public setListener(listener: (error: ErrorMessage) => void): void {
    this.listener = listener;
  }

  public addError(error: ErrorMessage): void {
    this.listener(error);
  }
}
