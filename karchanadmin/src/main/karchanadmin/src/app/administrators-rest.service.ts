import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Administrator } from './administrator/administrator.model';

@Injectable({
  providedIn: 'root'
})
export class AdministratorsRestService {
  url: string;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService) {
    this.url = environment.ADMINISTRATORS_URL;
  }

  get(): Observable<Administrator> {
    return this.http.get<Administrator>(this.url)
      .pipe(
        map(item => new Administrator(item)),
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
