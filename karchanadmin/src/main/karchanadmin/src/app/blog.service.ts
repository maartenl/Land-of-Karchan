import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import {catchError} from 'rxjs/operators';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {Blog} from './blogs/blog.model';

@Injectable({
  providedIn: 'root'
})
export class BlogService {
  url: string;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.BLOGS_URL;
  }

  public getBlogs(): Observable<any> {
    return this.http.get<Blog[]>(this.url)
    .pipe(
      catchError(err => {
        this.handleError(err);
        return [];
      })
    );
  }

  public deleteBlog(blog: Blog): any {
    return this.http.delete(this.url + '/' + blog.id)
    .pipe(
      catchError(err => {
        this.handleError(err);
        return [];
      })
    );
  }

  public updateBlog(blog: Blog): any {
    if (blog.id !== undefined && blog.id !== null) {
      // update
      return this.http.put<Blog[]>(this.url + '/' + blog.id, blog)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
    // new
    return this.http.post(this.url, blog)
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
