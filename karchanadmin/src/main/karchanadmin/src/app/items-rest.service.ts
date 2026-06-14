import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {environment} from '../environments/environment';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Item} from './items/item.model';
import {urls} from './urls';

@Injectable({
  providedIn: 'root',
})
export class ItemsRestService {
  private itemsUrl: string = urls.ITEMS_URL;
  private charactersUrl: string = urls.CHARACTERS_URL;
  private roomsUrl: string = urls.ROOMS_URL;
  private itemdefinitionsUrl: string = urls.ITEMDEFINITIONS_URL;

  private http = inject(HttpClient)
  private errorsService = inject(ErrorsService)

  public deleteIteminstance(item: Item): Observable<any> {
    return this.http.delete(this.itemsUrl + '/' + item.id + environment.postfix)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public createIteminstance(item: Item): any {
    // new
    return this.http.post(this.itemsUrl, item)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }


  /**
   * Retrieves all items in the inventory of this character.
   */
  public getAllItemsOfCharacter(name: String): Observable<Item[]> {
    return this.http.get<Item[]>(this.charactersUrl + '/' + name + '/items' + environment.postfix)
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
   * Retrieves all items within this room.
   */
  public getAllItemsInRoom(roomid: number): Observable<Item[]> {
    return this.http.get<Item[]>(this.roomsUrl + '/' + roomid + '/items' + environment.postfix)
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
   * Retrieves all items with this itemdefinition.
   */
  public getAllItemsOfItemdefinition(itemdefinitionid: number): Observable<Item[]> {
    return this.http.get<Item[]>(this.itemdefinitionsUrl + '/' + itemdefinitionid + '/items')
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
