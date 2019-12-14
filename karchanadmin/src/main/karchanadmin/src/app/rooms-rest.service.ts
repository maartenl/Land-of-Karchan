import { Observable, of, from } from 'rxjs';
import { catchError, publishReplay, refCount } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';


import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Room } from './rooms/room.model';
import { ErrorMessage } from './errors/errormessage.model';
import { Command } from './commands/command.model';

@Injectable({
  providedIn: 'root'
})
export class RoomsRestService {
  url: string;

  cache$: Observable<Room[]>;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.ROOMS_URL;
  }

  public getCount(owner: string): Observable<number> {
    const localUrl = owner === null || owner === undefined ? this.url + '/count' : this.url + '/count' + '?owner=' + owner;
    return this.http.get<Room[]>(localUrl)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getRoom(id: number): Observable<Room> {
    return this.http.get<Room>(this.url + '/' + id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommands(id: number): Observable<Command[]> {
    return this.http.get<Room>(this.url + '/' + id + '/commands')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getRooms(descriptionSearch: string): Observable<Room[]> {
    if (this.cache$) {
      return this.cache$;
    }
    const localUrl = descriptionSearch === undefined ? this.url : this.url + '?description=' + descriptionSearch;
    this.cache$ = this.http.get<Room[]>(localUrl)
      .pipe(
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

  public deleteRoom(room: Room): Observable<any> {
    return this.http.delete(this.url + '/' + room.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateRoom(room: Room): any {
    // update
    return this.http.put<Room[]>(this.url + '/' + room.id, room)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );

  }

  public createRoom(room: Room): any {
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
