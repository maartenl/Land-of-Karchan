import { Observable, of, from } from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import {Item, ItemDefinition} from './items/item.model';
import { AdminRestService } from './admin/admin-rest.service';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class ItemsRestService implements AdminRestService<ItemDefinition, number> {
  url: string;

  cache$: Observable<ItemDefinition[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.ITEMS_URL;
  }

  public get(id: number): Observable<ItemDefinition> {
    return this.http.get<ItemDefinition>(this.url + '/' + id)
      .pipe(
        map(item => new ItemDefinition(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getAll(): Observable<ItemDefinition[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all items.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<ItemDefinition[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<ItemDefinition>();
          items.forEach(item => newItems.push(new ItemDefinition(item)));
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


  /**
   * Retrieves all items with this itemdefinition.
   */
  public getAllItems(itemdefinitionid: number): Observable<Item[]> {
    return this.http.get<Item[]>(this.url + '/' + itemdefinitionid + '/items')
      .pipe(
        map(items => {
          const newItems = new Array<Item>();
          items.forEach(item => newItems.push(new Item(item)));
          return newItems;
        }),
        publishReplay(1),
        refCount(),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public clearCache() {
    this.cache$ = null;
  }

  public delete(item: ItemDefinition): Observable<any> {
    return this.http.delete(this.url + '/' + item.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(item: ItemDefinition): any {
    // update
    return this.http.put<ItemDefinition[]>(this.url + '/' + item.id, item)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public create(item: ItemDefinition): any {
    // new
    return this.http.post(this.url, item)
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
