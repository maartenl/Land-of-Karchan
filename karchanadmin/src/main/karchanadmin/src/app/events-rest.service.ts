import {Observable, of, from, share, ReplaySubject} from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { AdminRestService } from './admin/admin-rest.service';
import { ToastService } from './toast.service';
import { MudEvent } from './events/event.model';

@Injectable({
  providedIn: 'root'
})
export class EventsRestService implements AdminRestService<MudEvent, number>  {

  url: string;

  cache$: Observable<MudEvent[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.EVENTS_URL;
  }

  get(eventid: number): Observable<MudEvent> {
    return this.http.get<MudEvent>(this.url + '/' + eventid)
      .pipe(
        map(item => new MudEvent(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<MudEvent[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all events.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<MudEvent[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<MudEvent>();
          items.forEach(item => newItems.push(new MudEvent(item)));
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

  delete(event: MudEvent): Observable<any> {
    return this.http.delete(this.url + '/' + event.eventid)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(event: MudEvent) {
    // update
    return this.http.put<MudEvent[]>(this.url + '/' + event.eventid, event)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(event: MudEvent) {
    // new
    return this.http.post(this.url, event)
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
