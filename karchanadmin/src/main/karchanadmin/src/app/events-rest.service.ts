import {inject, Injectable} from '@angular/core';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Command} from './commands/command.model';
import {AdminRestService} from './admin/admin-rest.service';
import {MudEvent} from './events/event.model';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class EventsRestService extends AdminRestService<MudEvent, number> {
  url: string = urls.EVENTS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<MudEvent[]> | null = null;

  get(eventid: number): Observable<MudEvent> {
    return this.http.get<MudEvent>(this.url + '/' + eventid + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all events.", "Loading...");
    this.cache$ = this.http.get<MudEvent[]>(this.url + environment.postfix)
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
}
