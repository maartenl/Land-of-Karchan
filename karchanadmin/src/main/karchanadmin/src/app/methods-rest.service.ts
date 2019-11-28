import { Observable, of, from } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, map, tap } from 'rxjs/operators';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Method } from './methods/method.model';
import { ErrorMessage } from './errors/errormessage.model';

@Injectable({
  providedIn: 'root'
})
export class MethodsRestService {
  url: string;

  methods = new Array<Method>();

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.METHODS_URL;
    if (environment.production === false) {
      if (window.console) { console.log('methods-rest.service constructor'); }
      for (let i = 0; i < 7000; i++) {
        const method: Method = {
          name: 'method' + i,
          src: 'scriptiebit',
          owner: 'Karn',
          creation: new Date() + ''
        };
        this.methods.push(method);
      }
    }
  }

  public getCount(): Observable<number> {
    if (environment.production === false) {
      return of(this.methods.length);
    }
    return this.http.get<Method[]>(this.url + '/count')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getMethod(name: string): Observable<Method> {
    if (environment.production === false) {
      const foundMethod = this.methods.find(method => method.name === name);
      return of(foundMethod);
    }
    return this.http.get<Method>(this.url + '/' + name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getMethods(startRow: number, endRow: number): Observable<Method[]> {
    if (environment.production === false) {
      if (window.console) {
        console.log('methods-rest.service getMethods start: ' + startRow + ' end: ' + endRow);
      }
      const slice = this.methods.slice(startRow, endRow);
      return of(slice);
    }
    return this.http.get<Method[]>(this.url + '/'  + startRow + '/' + (endRow - startRow))
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public deleteMethod(method: Method): Observable<any> {
    if (environment.production === false) {
      const index = this.methods.findIndex(lmethod => lmethod.name === method.name);
      if (index !== -1) {
        this.methods.splice(index, 1);
      }
      return of(method);
    }
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
      if (environment.production === false) {
        const index = this.methods.findIndex(lmethod => lmethod.name === method.name);
        if (index !== -1) {
          this.methods[index] = method;
        }
        return of(method);
      }
      // update
      return this.http.put<Method[]>(this.url + '/' + method.name, method)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
    // new
    if (environment.production === false) {
      this.methods.push(method);
      return of(method);
    }
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
