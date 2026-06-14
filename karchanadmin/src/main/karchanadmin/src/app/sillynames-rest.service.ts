import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';
import {Sillyname} from './banishment/sillynames.model';

@Injectable({
  providedIn: 'root',
})
export class SillynamesRestService extends AdminRestService<Sillyname, string> {
  url: string = urls.BAN_URL + '/sillynames';

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Sillyname[]> | null = null;

  override get(id: string): Observable<Sillyname> {
    return this.cache$?.pipe(map(items => items.filter(item => item.name == id)[0]))
      ?? new Observable<Sillyname>(() => {
        new Sillyname();
      });
  }

  override getAll(): Observable<Sillyname[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all sillynames.", "Loading...");
    this.cache$ = this.http.get<Sillyname[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Sillyname>();
          items.forEach(item => newItems.push(new Sillyname(item)));
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

  override delete(item: Sillyname): Observable<any> {
    return this.http.delete(this.url + '/' + item.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  override update(item: Sillyname) {
    throw new Error("Method not implemented.");
  }

  override create(item: Sillyname) {
    // new
    return this.http.post(this.url, item)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

}
