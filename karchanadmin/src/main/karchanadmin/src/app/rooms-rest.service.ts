import {inject, Injectable} from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, of, ReplaySubject, share} from 'rxjs';
import {Room} from './rooms/room.model';
import {Command} from './commands/command.model';
import {MudCharacter} from './characters/character.model';
import {urls} from './urls';
import {AdminRestService} from './admin/admin-rest.service';

@Injectable({
  providedIn: 'root',
})
export class RoomsRestService extends AdminRestService<Room, number> {
  url: string = urls.ROOMS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Room[]> | null = null;

  public get(id: number): Observable<Room> {
    return this.http.get<Room>(this.url + '/' + id + environment.postfix)
      .pipe(
        map(item => new Room(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommands(id: number): Observable<Command[]> {
    return this.http.get<Command[]>(this.url + '/' + id + '/commands' + environment.postfix)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCharacters(id: number): Observable<MudCharacter[]> {
    return this.http.get<MudCharacter[]>(this.url + '/' + id + '/characters' + environment.postfix)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getAll(): Observable<Room[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all rooms.", "Loading...");
    this.cache$ = this.http.get<Room[]>(this.url + environment.postfix)
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

}
