import {inject, Injectable} from '@angular/core';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {AdminRestService} from './admin/admin-rest.service';
import {environment} from '../environments/environment';
import {Template} from './templates/template.model';

@Injectable({
  providedIn: 'root',
})
export class TemplatesRestService extends AdminRestService<Template, number> {
  url: string = urls.TEMPLATES_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Template[]> | null = null;

  public get(id: number): Observable<Template> {
    return this.http.get<Template>(this.url + '/' + id + environment.postfix)
      .pipe(
        map(item => new Template(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public clearCache() {
    this.cache$ = null;
  }

  public getAll(): Observable<Template[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all Templates.", "Loading...");
    this.cache$ = this.http.get<Template[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Template>();
          items.forEach(item => newItems.push(new Template(item)));
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

  public delete(template: Template): Observable<any> {
    return this.http.delete(this.url + '/' + template.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(template: Template): any {
    if (template.name !== undefined) {
      // update
      return this.http.put<Template[]>(this.url + '/' + template.id, template)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
  }

  public create(template: Template): any {
    // new
    return this.http.post(this.url, template)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getHistoricTemplates(template: Template): Observable<Template[]> {
    return this.http.get<Template[]>(this.url+ '/' + template.id + '/history' + environment.postfix)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

}
