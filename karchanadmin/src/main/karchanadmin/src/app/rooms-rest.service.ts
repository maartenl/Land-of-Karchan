import { Observable, of } from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { AdminRestService } from './admin/admin-rest.service';
import { ErrorsService } from './errors.service';
import { Room } from './rooms/room.model';
import { Command } from './commands/command.model';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class RoomsRestService implements AdminRestService<Room, number> {
  url: string;

  cache$: Observable<Room[]>;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.ROOMS_URL;
  }

  public get(id: number): Observable<Room> {
    if (!environment.production) {
      return this.getAll(null).pipe(
        map(x => x.filter(item => item.getIdentifier() === id)[0])
      );
    }
    return this.http.get<Room>(this.url + '/' + id)
      .pipe(
        map(item => new Room(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommands(id: number): Observable<Command[]> {
    if (!environment.production) {
      return of(Command[0]);
    }
    return this.http.get<Room>(this.url + '/' + id + '/commands')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getAll(descriptionSearch: string): Observable<Room[]> {
    if (this.cache$) {
      return this.cache$;
    }
    const localUrl = descriptionSearch === undefined ? this.url : this.url + '?description=' + descriptionSearch;
    this.toastService.show('Getting rooms and other things that we really need', {
      delay: 0,
      autohide: false,
      headertext: 'Loading...',
      classname: 'mytoast'
    });

    this.cache$ = this.http.get<Room[]>(localUrl)
      .pipe(
        map(items => {
          const newItems = new Array<Room>();
          items.forEach(item => newItems.push(new Room(item)));
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

  public clearCache() {
    this.cache$ = undefined;
  }

  public delete(room: Room): Observable<any> {
    return this.http.delete(this.url + '/' + room.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(room: Room): any {
    // update
    return this.http.put<Room[]>(this.url + '/' + room.id, room)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );

  }

  public create(room: Room): any {
    // new
    return this.http.post(this.url, room)
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
