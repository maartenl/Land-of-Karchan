import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {environment} from '../environments/environment';
import {Administrator} from './currentadmin/administrator.model';
import {catchError, map, Observable} from 'rxjs';
import {urls} from './urls';

@Injectable({
  providedIn: 'root',
})
export class AdministratorsRestService {
  url: string = urls.ADMINISTRATORS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);

  get(): Observable<Administrator> {
    return this.http.get<Administrator>(this.url + environment.postfix)
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
