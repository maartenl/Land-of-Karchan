import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {Method} from './methods/method.model';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Area} from './areas/area.model';
import {environment} from '../environments/environment';
import {Command} from './commands/command.model';

@Injectable({
  providedIn: 'root',
})
export class MethodsRestService extends AdminRestService<Method, string> {
  url: string = urls.METHODS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Method[]> | null = null;


  public get(name: string): Observable<Method> {
    return this.http.get<Method>(this.url + '/' + name + environment.postfix)
      .pipe(
        map(item => new Method(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommands(name: string): Observable<Command[]> {
    return this.http.get<Command[]>(this.url + '/' + name + '/commands' + environment.postfix)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public clearCache() {
    this.cache$ = null;
  }

  public getAll(): Observable<Method[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all methods.", "Loading...");
    this.cache$ = this.http.get<Method[]>(this.url+ environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Method>();
          items.forEach(item => newItems.push(new Method(item)));
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

  public delete(method: Method): Observable<any> {
    return this.http.delete(this.url + '/' + method.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(method: Method): any {
    if (method.name !== undefined) {
      // update
      return this.http.put<Method[]>(this.url + '/' + method.name, method)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
  }

  public create(method: Method): any {
    // new
    return this.http.post(this.url, method)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

}
