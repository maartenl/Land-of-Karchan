import {inject, Injectable} from '@angular/core';
import {urls} from './urls';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {Systemlog} from './systemlogs/systemlog.model';
import {catchError, Observable} from 'rxjs';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class SystemlogsRestService {
  url: string = urls.SYSTEMLOG_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  public getLogs(name: string | null, fromDate: string | null, toDate: string | null): Observable<any> {
    var url = this.url;
    var params: string[] = [];
    if (name !== null) {
      params.push('name=' + name);
    }
    if (fromDate !== null) {
      params.push('from=' + fromDate);
    }
    if (toDate !== null) {
      params.push('to=' + toDate);
    }
    if (params.length > 0) {
      url = url + environment.postfix + "?" + params.join("&");
    }
    this.toastService.showRetrieving("Retrieving system logs.", "Loading...");
    return this.http.get<Systemlog[]>(url)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  /**
   * Handles error, delivers them to the errorService.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  private handleError(error: HttpErrorResponse, ignore?: string[]) {
    this.errorsService.addHttpError(error, ignore);
  }
}
