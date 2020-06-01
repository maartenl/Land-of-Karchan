import { Injectable } from '@angular/core';
import { Guild } from './guilds/guild.model';
import { Observable } from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { ErrorsService } from './errors.service';
import { AdminRestService } from './admin/admin-rest.service';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class GuildsRestService implements AdminRestService<Guild, string> {
  url: string;

  cache$: Observable<Guild[]>;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = '/karchangame/resources/administration/guilds';
  }

  get(id: string): Observable<Guild> {
    return this.http.get<Guild>(this.url + '/' + id)
      .pipe(
        map(item => new Guild(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<Guild[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all guilds.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<Guild[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<Guild>();
          items.forEach(item => newItems.push(new Guild(item)));
          return newItems;
        }),
        publishReplay(1),
        refCount(),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
    return this.cache$;
  }

  clearCache() {
    this.cache$ = undefined;
  }

  delete(guild: Guild): Observable<any> {
    return this.http.delete(this.url + '/' + guild.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(guild: Guild) {
    // update
    return this.http.put<Guild[]>(this.url + '/' + guild.name, guild)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(guild: Guild) {
    // new
    return this.http.post(this.url, guild)
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
