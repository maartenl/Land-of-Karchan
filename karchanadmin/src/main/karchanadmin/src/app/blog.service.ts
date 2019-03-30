import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, map, tap } from 'rxjs/operators';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Blog } from './blogs/blog.model';
import { ErrorMessage } from './errors/errormessage.model';

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
    console.log(blog);
    if (blog.id !== undefined) {
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

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.message;
      errormessage.type = 'Network Error';
      this.errorsService.addError(errormessage);
  } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.errormessage;
      if (error.error.errormessage === undefined) {
        errormessage.message = error.statusText;
      }
      errormessage.type = error.status.toString();
      this.errorsService.addError(errormessage);
    }
  }
}
