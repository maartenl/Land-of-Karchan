import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {Manpage} from './manpages/manpage.model';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ManpagesRestService  extends AdminRestService<Manpage, string>{
  url: string = urls.MANPAGES_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Manpage[]> | null = null;

  get(manpage: string): Observable<Manpage> {
    return this.http.get<Manpage>(this.url + '/' + manpage + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all manpages.", "Loading...");
    this.cache$ = this.http.get<Manpage[]>(this.url + environment.postfix)
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
}
