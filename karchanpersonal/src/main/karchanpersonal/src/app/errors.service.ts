import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { ToastService } from './toast.service';
import { ErrorMessage } from './errors/errormessage.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorsService {
  private listener: (error: ErrorMessage) => void;

  constructor(private toastService: ToastService) { }

  public setListener(listener: (error: ErrorMessage) => void): void {
    this.listener = listener;
  }

  public addError(error: ErrorMessage): void {
    this.toastService.show(error.message, {
      delay: 0,
      autohide: false,
      headertext: error.type,
      classname: 'bg-danger text-light'
    });

  }

  /**
   * Addds a http (network) error.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  public addHttpError(error: HttpErrorResponse, ignore?: string[]) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.message;
      errormessage.type = 'Network Error';
      this.addError(errormessage);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      if (ignore !== undefined && ignore.includes(error.status.toString())) {
        return;
      }
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.errormessage;
      if (error.error.errormessage === undefined) {
        errormessage.message = error.statusText;
      }
      errormessage.type = error.status.toString();
      this.addError(errormessage);
    }
  }

}
