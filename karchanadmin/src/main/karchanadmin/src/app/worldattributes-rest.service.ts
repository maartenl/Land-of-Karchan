import {Observable, ReplaySubject, share} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {environment} from '../environments/environment';

import {AdminRestService} from './admin/admin-rest.service';
import {ErrorsService} from './errors.service';
import {Worldattribute} from './worldattributes/worldattribute.model';
import {ToastService} from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class WorldattributesRestService implements AdminRestService<Worldattribute, string> {

  url: string;

  cache$: Observable<Worldattribute[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.WORLDATTRIBUTES_URL;
  }

  public get(id: string): Observable<Worldattribute> {
    if (!environment.production) {
      return this.getAll().pipe(
        map(x => x.filter(item => item.getIdentifier() === id)[0])
      );
    }
    return this.http.get<Worldattribute>(this.url + '/' + id)
      .pipe(
        map(item => new Worldattribute(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getAll(): Observable<Worldattribute[]> {
    if (this.cache$) {
      return this.cache$;
    }
    const localUrl = this.url;
    this.toastService.show('Retrieving all worldattributes.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });

    this.cache$ = this.http.get<Worldattribute[]>(localUrl)
      .pipe(
        map(items => {
          const newItems = new Array<Worldattribute>();
          items.forEach(item => newItems.push(new Worldattribute(item)));
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

  public clearCache() {
    this.cache$ = null;
  }

  public delete(worldattribute: Worldattribute): Observable<any> {
    return this.http.delete(this.url + '/' + worldattribute.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(worldattribute: Worldattribute): any {
    // update
    return this.http.put<Worldattribute[]>(this.url + '/' + worldattribute.name, worldattribute)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );

  }

  public create(worldattribute: Worldattribute): any {
    // new
    return this.http.post(this.url, worldattribute)
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
