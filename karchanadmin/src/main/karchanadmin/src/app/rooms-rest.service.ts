import { Observable, of, from } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, map, tap } from 'rxjs/operators';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Room } from './rooms/room.model';
import { ErrorMessage } from './errors/errormessage.model';
import { Page } from './page.model';
import { PagedData } from './paged-data.model';

@Injectable({
  providedIn: 'root'
})
export class RoomsRestService {
  url: string;

  rooms = new Array<Room>();

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    if (window.console) { console.log('rooms-rest.service constructor'); }
    this.url = environment.ROOMS_URL;
    if (environment.production === false) {
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
    }
  }
  public getAllRooms(): Observable<Room[]> {
    const rooms = this.rooms;
    if (environment.production === false) {
      return from([rooms]);
    }
    return this.http.get<Room[]>(this.url)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getRooms(startRow: number, endRow: number): Observable<Room[]> {
    if (window.console) {
      console.log('rooms-rest.service getRooms start: ' + startRow + ' end: ' + endRow);
    }
    if (environment.production === false) {
      return of(this.rooms.slice(startRow, endRow));
    }
    return this.http.get<Room[]>(this.url)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  /**
   * Package rooms into a PagedData object based on the selected Page
   * @param page The page data used to get the selected data from rooms
   * @returns An array of the selected data and page
   */
  private getPagedData(page: Page): PagedData<Room> {
    if (window.console) {
      console.log('entering getPagedData');
      console.log(page);
    }
    const pagedData = new PagedData<Room>();
    page.totalElements = this.rooms.length;
    page.totalPages = page.totalElements / page.size;
    const start = page.pageNumber * page.size;
    const end = Math.min(start + page.size, page.totalElements);
    for (let i = start; i < end; i++) {
      const jsonObj = this.rooms[i];
      const room = new Room();
      room.id = jsonObj.id;
      room.west = jsonObj.west;
      room.east = jsonObj.east;
      room.north = jsonObj.north;
      room.south = jsonObj.south;
      room.up = jsonObj.up;
      room.down = jsonObj.down;
      room.contents = jsonObj.contents;
      room.owner = jsonObj.owner;
      room.creation = jsonObj.creation;
      room.area = jsonObj.area;
      room.title = jsonObj.title;
      room.picture = jsonObj.picture;
      if (window.console) { console.log(pagedData); }
      pagedData.data.push(room);
    }
    pagedData.page = page;
    if (window.console) {
      console.log('leaving getPagedData');
      console.log(pagedData);
    }
    return pagedData;
  }

  public deleteRoom(room: Room): any {
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
