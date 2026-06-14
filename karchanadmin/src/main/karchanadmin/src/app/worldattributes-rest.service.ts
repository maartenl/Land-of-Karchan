import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {Worldattribute} from './worldattributes/worldattribute.model';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class WorldattributesRestService extends AdminRestService<Worldattribute, string> {
  url: string = urls.WORLDATTRIBUTES_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Worldattribute[]> | null = null;

  public get(id: string): Observable<Worldattribute> {
    return this.http.get<Worldattribute>(this.url + '/' + id + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all worldattributes.", "Loading...");
    this.cache$ = this.http.get<Worldattribute[]>(this.url + environment.postfix)
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

}
