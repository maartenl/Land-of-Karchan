import {Observable} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {Item, ItemDefinition} from './items/item.model';

/**
 * Anything regarding Rest calls for creating and retrieving and deleteing Items (instances of Item Definitions, basically).
 */
@Injectable({
  providedIn: 'root'
})
export class ItemsRestService {

  cache$: Observable<ItemDefinition[]> | null = null;
  private itemsUrl: string;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService) {
    this.itemsUrl = environment.ITEMS_URL;
  }

  public deleteIteminstance(item: Item): Observable<any> {
    return this.http.delete(this.itemsUrl + '/' + item.id)
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
   * Handles error, delivers them to the errorService.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  private handleError(error: HttpErrorResponse, ignore?: string[]) {
    this.errorsService.addHttpError(error, ignore);
  }
}
