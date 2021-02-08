import { Observable, of, from } from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Area } from './areas/area.model';
import { AdminRestService } from './admin/admin-rest.service';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class AreasRestService implements AdminRestService<Area, string> {
  url: string;

  cache$: Observable<Area[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.AREAS_URL;
  }

  get(area: string): Observable<Area> {
    return this.http.get<Area>(this.url + '/' + area)
      .pipe(
        map(item => new Area(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<Area[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all areas.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<Area[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<Area>();
          items.forEach(item => newItems.push(new Area(item)));
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

  clearCache() {
    this.cache$ = null;
  }

  delete(area: Area): Observable<any> {
    return this.http.delete(this.url + '/' + area.area)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(area: Area) {
    // update
    return this.http.put<Area[]>(this.url + '/' + area.area, area)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(area: Area) {
    // new
    return this.http.post(this.url, area)
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
