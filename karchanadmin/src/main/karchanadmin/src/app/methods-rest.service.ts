import { Observable, of, from } from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Method } from './methods/method.model';
import { Command } from './commands/command.model';
import { AdminRestService } from './admin/admin-rest.service';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class MethodsRestService implements AdminRestService<Method, string> {
  url: string;

  cache$: Observable<Method[]>;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.METHODS_URL;
  }

  public get(name: string): Observable<Method> {
    return this.http.get<Method>(this.url + '/' + name)
      .pipe(
        map(item => new Method(item)),
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

  public getAll(): Observable<Method[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all methods.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<Method[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<Method>();
          items.forEach(item => newItems.push(new Method(item)));
          return newItems;
        }),
        publishReplay(1),
        refCount(),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
    return this.cache$;
  }

  public delete(method: Method): Observable<any> {
    return this.http.delete(this.url + '/' + method.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(method: Method): any {
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

  public create(method: Method): any {
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
