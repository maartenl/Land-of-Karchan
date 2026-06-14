import {inject, Injectable} from '@angular/core';
import {ToastService} from './toast.service';
import {ErrorMessage} from './errors/errormessage.model';
import {HttpErrorResponse} from '@angular/common/http';
import {Logger} from './consolelog.service';

@Injectable({
  providedIn: 'root',
})
export class ErrorsService {

  toastService = inject(ToastService);

  public addError(error: ErrorMessage): void {
    this.toastService.showError(error.message === null ? 'Unknown message.' : error.message, error.type);
  }

  /**
   * Addds a http (network) error.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  public addHttpError(error: HttpErrorResponse, ignore?: string[]) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      Logger.logError('An error occurred:', error.error.message);
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.message;
      errormessage.type = 'Network Error';
      this.addError(errormessage);
    } else {
      Logger.logError('An unknown error occurred:', error);
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      if (ignore !== undefined && ignore.includes(error.status.toString())) {
        return;
      }
      const errormessage = new ErrorMessage();
      if (error.error !== undefined) {
        errormessage.message = error.error.errormessage;
        if (error.error.errormessage === undefined) {
          errormessage.message = error.statusText;
        }
      } else { errormessage.message = error.statusText; }
      errormessage.type = error.status.toString();
      this.addError(errormessage);
    }
  }
}
