import {inject, Injectable} from '@angular/core';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {AdminRestService} from './admin/admin-rest.service';
import {BannedIP} from './banishment/banned.model';
import {environment} from '../environments/environment';
import {Method} from './methods/method.model';

@Injectable({
  providedIn: 'root',
})
export class BannedipsRestService extends AdminRestService<BannedIP, string> {
  url: string = urls.BAN_URL + '/bannedips';

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<BannedIP[]> | null = null;

  override get(id: string): Observable<BannedIP> {
    return this.cache$?.pipe(map(items => items.filter(item => item.address == id)[0]))
      ?? new Observable<BannedIP>(() => {
        new BannedIP();
      });
  }

  override getAll(): Observable<BannedIP[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all banned ips.", "Loading...");
    this.cache$ = this.http.get<BannedIP[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<BannedIP>();
          items.forEach(item => newItems.push(new BannedIP(item)));
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

  override delete(item: BannedIP): Observable<any> {
    return this.http.delete(this.url + '/' + item.address)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  override update(item: BannedIP) {
    throw new Error("Method not implemented.");
  }

  override create(item: BannedIP) {
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
