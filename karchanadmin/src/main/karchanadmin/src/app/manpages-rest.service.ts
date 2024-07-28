import {Observable, ReplaySubject, share} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {AdminRestService} from './admin/admin-rest.service';
import {ToastService} from './toast.service';
import {Manpage} from './manpages/manpage.model';

@Injectable({
  providedIn: 'root'
})
export class ManpagesRestService implements AdminRestService<Manpage, string> {

  url: string;

  cache$: Observable<Manpage[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.MANPAGES_URL;
  }

  get(manpage: string): Observable<Manpage> {
    return this.http.get<Manpage>(this.url + '/' + manpage)
      .pipe(
        map(item => new Manpage(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<Manpage[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all manpages.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<Manpage[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<Manpage>();
          items.forEach(item => newItems.push(new Manpage(item)));
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

  delete(manpage: Manpage): Observable<any> {
    return this.http.delete(this.url + '/' + manpage.command)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(manpage: Manpage) {
    // update
    return this.http.put<Manpage[]>(this.url + '/' + manpage.command, manpage)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(manpage: Manpage) {
    // new
    return this.http.post(this.url, manpage)
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
