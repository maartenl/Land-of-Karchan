import { Observable, of, from } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, map, tap } from 'rxjs/operators';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Room } from './rooms/room.model';
import { ErrorMessage } from './errors/errormessage.model';

@Injectable({
  providedIn: 'root'
})
export class RoomsRestService {
  url: string;

  rooms = new Array<Room>();

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.ROOMS_URL;
    if (environment.production === false) {
      if (window.console) { console.log('rooms-rest.service constructor'); }
      for (let i = 0; i < 7000; i++) {
        const room: Room = {
          id: i,
          west: 4,
          east: 6,
          north: null,
          south: null,
          up: null,
          down: null,
          owner: 'Karn',
          contents: 'You are in a dark room',
          area: 'Main',
          creation: new Date() + '',
          picture: '/picture.jpg',
          title: 'A dark room'
        };
        this.rooms.push(room);
      }
      this.rooms[0].north = 1;
      this.rooms[1].north = 2;
      this.rooms[1].south = 0;
    }
  }

  public getCount(): Observable<number> {
    if (environment.production === false) {
      return of(this.rooms.length);
    }
    return this.http.get<Room[]>(this.url + '/count')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getRoom(id: number): Observable<Room> {
    if (environment.production === false) {
      const foundRoom = this.rooms.find(room => room.id === id);
      return of(foundRoom);
    }
    return this.http.get<Room>(this.url + '/' + id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getRooms(startRow: number, endRow: number): Observable<Room[]> {
    if (environment.production === false) {
      if (window.console) {
        console.log('rooms-rest.service getRooms start: ' + startRow + ' end: ' + endRow);
      }
      const slice = this.rooms.slice(startRow, endRow);
      return of(slice);
    }
    return this.http.get<Room[]>(this.url + '/'  + startRow + '/' + (endRow - startRow))
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public deleteRoom(room: Room): Observable<any> {
    if (environment.production === false) {
      const index = this.rooms.findIndex(lroom => lroom.id === room.id);
      if (index !== -1) {
        this.rooms.splice(index, 1);
      }
      return of(room);
    }
    return this.http.delete(this.url + '/' + room.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateRoom(room: Room): any {
    if (room.id !== undefined) {
      if (environment.production === false) {
        const index = this.rooms.findIndex(lroom => lroom.id === room.id);
        if (index !== -1) {
          this.rooms[index] = room;
        }
        return of(room);
      }
      // update
      return this.http.put<Room[]>(this.url + '/' + room.id, room)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
    // new
    if (environment.production === false) {
      this.rooms.push(room);
      return of(room);
    }
    return this.http.post(this.url, room)
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