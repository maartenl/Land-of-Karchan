import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {Area} from './areas/area.model';
import {environment} from '../environments/environment';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {urls} from './urls';

@Injectable({
  providedIn: 'root',
})
export class AreasRestService extends AdminRestService<Area, string> {
  url: string = urls.AREAS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Area[]> | null = null;


  get(area: string): Observable<Area> {
    return this.http.get<Area>(this.url + '/' + area + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all areas.", "Loading...");
    this.cache$ = this.http.get<Area[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Area>();
          items.forEach(item => newItems.push(new Area(item)));
          return newItems;
        }),
        share({
          connector: () => new ReplaySubject(1),
          resetOnError: false,
          resetOnComplete: false,
          resetOnRefCountZero: false
        }),
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

}
