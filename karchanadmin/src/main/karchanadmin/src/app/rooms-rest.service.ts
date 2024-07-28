import {Observable, of, ReplaySubject, share} from 'rxjs';
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
import {Item} from "./items/item.model";

@Injectable({
  providedIn: 'root'
})
export class RoomsRestService implements AdminRestService<Room, number> {
  url: string;

  cache$: Observable<Room[]> | null = null;

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
      return of(new Array<Command>(0));
    }
    return this.http.get<Command[]>(this.url + '/' + id + '/commands')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getAll(descriptionSearch: string | null): Observable<Room[]> {
    if (this.cache$) {
      return this.cache$;
    }
    const localUrl = descriptionSearch === undefined || descriptionSearch === null ? this.url : this.url + '?description=' + descriptionSearch;
    this.toastService.show('Retrieving all rooms.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });

    this.cache$ = this.http.get<Room[]>(localUrl)
      .pipe(
        map(items => {
          const newItems = new Array<Room>();
          items.forEach(item => newItems.push(new Room(item)));
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

  public clearCache() {
    this.cache$ = null;
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
   * Retrieves all items within this room.
   */
  public getAllItems(roomid: number): Observable<Item[]> {
    return this.http.get<Item[]>(this.url + '/' + roomid + '/items')
      .pipe(
        map(items => {
          const newItems = new Array<Item>();
          items.forEach(item => newItems.push(new Item(item)));
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
