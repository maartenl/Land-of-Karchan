import { Observable, of, from } from 'rxjs';
import { catchError, publishReplay, refCount } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Method } from './methods/method.model';
import { Command } from './commands/command.model';

@Injectable({
  providedIn: 'root'
})
export class MethodsRestService {
  url: string;

  cache$: Observable<Method[]>;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.METHODS_URL;
  }

  public getMethod(name: string): Observable<Method> {
    return this.http.get<Method>(this.url + '/' + name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommands(name: string): Observable<Command[]> {
    return this.http.get<Method>(this.url + '/' + name + '/commands')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public clearCache() {
    this.cache$ = undefined;
  }

  public getMethods(): Observable<Method[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.cache$ = this.http.get<Method[]>(this.url)
      .pipe(
        publishReplay(1),
        refCount(),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
    return this.cache$;
  }

  public deleteMethod(method: Method): Observable<any> {
    return this.http.delete(this.url + '/' + method.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateMethod(method: Method): any {
    if (method.name !== undefined) {
      // update
      return this.http.put<Method[]>(this.url + '/' + method.name, method)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
  }

  public createMethod(method: Method): any {
    // new
    return this.http.post(this.url, method)
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
