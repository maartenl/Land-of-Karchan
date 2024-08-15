import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {catchError} from 'rxjs/operators';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {Template} from './templates/template.model';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {
  url: string;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.TEMPLATES_URL;
  }

  public getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(this.url)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getHistoricTemplates(template: Template): Observable<Template[]> {
    return this.http.get<Template[]>(this.url+ '/' + template.id + '/history')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateTemplate(template: Template): any {
    // update
    return this.http.put<Template[]>(this.url + '/' + template.id, template)
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
