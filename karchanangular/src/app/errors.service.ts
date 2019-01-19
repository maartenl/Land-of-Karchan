import { Injectable } from '@angular/core';

import { Error } from './errors/error.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {
  private listener: (error: Error) => void;

  constructor() { }

  public setListener(listener: (error: Error) => void): void {
    this.listener = listener;
  }

  public addError(error: Error): void {
    this.listener(error);
  }
}
