import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';
import {Unbannedname} from './banishment/unbannedname.model';

@Injectable({
  providedIn: 'root',
})
export class UnbannednameRestService extends AdminRestService<Unbannedname, string> {
  url: string = urls.BAN_URL + '/unbannednames';

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Unbannedname[]> | null = null;

  override get(id: string): Observable<Unbannedname> {
    return this.cache$?.pipe(map(items => items.filter(item => item.name == id)[0]))
      ?? new Observable<Unbannedname>(() => {
        new Unbannedname();
      });
  }

  override getAll(): Observable<Unbannedname[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all Unbannednames.", "Loading...");
    this.cache$ = this.http.get<Unbannedname[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Unbannedname>();
          items.forEach(item => newItems.push(new Unbannedname(item)));
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

  override delete(item: Unbannedname): Observable<any> {
    return this.http.delete(this.url + '/' + item.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  override update(item: Unbannedname) {
    throw new Error("Method not implemented.");
  }

  override create(item: Unbannedname) {
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
