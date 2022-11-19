import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {catchError} from 'rxjs/operators';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {Systemlog} from './systemlog/systemlog.model';

@Injectable({
  providedIn: 'root'
})
export class SystemlogService {
  url: string;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.SYSTEMLOG_URL;
  }

  public getLogs(name: string | null, creation: string | null): Observable<any> {
    var url = this.url;
    if (name !== null) {
      if (creation !== null) {
        url = url + '?name=' + name + '&creation=' + creation;
      } else {
        url = url + '?name=' + name;
      }
    } else if (creation !== null) {
      url = url + '?creation=' + creation;
    }
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
