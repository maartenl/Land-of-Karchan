import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';
import {Bannedname} from './banishment/bannednames.model';

@Injectable({
  providedIn: 'root',
})
export class BannednamesRestService extends AdminRestService<Bannedname, string> {
  url: string = urls.BAN_URL + '/bannednames';

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Bannedname[]> | null = null;

  override get(id: string): Observable<Bannedname> {
    return this.cache$?.pipe(map(items => items.filter(item => item.name == id)[0]))
      ?? new Observable<Bannedname>(() => {
        new Bannedname();
      });
  }

  override getAll(): Observable<Bannedname[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all banned ips.", "Loading...");
    this.cache$ = this.http.get<Bannedname[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Bannedname>();
          items.forEach(item => newItems.push(new Bannedname(item)));
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

  override delete(item: Bannedname): Observable<any> {
    return this.http.delete(this.url + '/' + item.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  override update(item: Bannedname) {
    throw new Error("Method not implemented.");
  }

  override create(item: Bannedname) {
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
