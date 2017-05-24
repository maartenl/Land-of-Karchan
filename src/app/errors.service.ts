import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

import { Error } from './errors/error.model';

@Injectable()
export class ErrorsService {
  private listener: (error: Error) => void;

  constructor() { }

  public setListener(listener: (error: Error) => void): void {
    console.log("ErrorrsService -> setListener");
    this.listener = listener;
  }

  public addError(error: Error) : void {
    console.log("ErrorsService -> addError", error, this.listener);
    console.log(this);
    this.listener(error);
  }

}
