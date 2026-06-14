import {inject, Injectable} from '@angular/core';
import {Blog} from './blogs/blog.model';
import {AdminRestService} from './admin/admin-rest.service';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BlogsRestService extends AdminRestService<Blog, number> {
  url: string = urls.BLOGS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Blog[]> | null = null;

  public get(id: number): Observable<Blog> {
    return this.http.get<Blog>(this.url + '/' + id + environment.postfix)
      .pipe(
        map(item => new Blog(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public clearCache() {
    this.cache$ = null;
  }

  public getAll(): Observable<Blog[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all Blogs.", "Loading...");
    this.cache$ = this.http.get<Blog[]>(this.url+ environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Blog>();
          items.forEach(item => newItems.push(new Blog(item)));
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

  public delete(Blog: Blog): Observable<any> {
    return this.http.delete(this.url + '/' + Blog.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(Blog: Blog): any {
    if (Blog.name !== undefined) {
      // update
      return this.http.put<Blog[]>(this.url + '/' + Blog.id, Blog)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
  }

  public create(Blog: Blog): any {
    // new
    return this.http.post(this.url, Blog)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

}
