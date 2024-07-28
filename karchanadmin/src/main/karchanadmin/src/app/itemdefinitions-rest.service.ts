import {Injectable} from '@angular/core';
import {AdminRestService} from "./admin/admin-rest.service";
import {Item, ItemDefinition} from "./items/item.model";
import {Observable, ReplaySubject, share} from "rxjs";
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {ErrorsService} from "./errors.service";
import {ToastService} from "./toast.service";
import {environment} from "../environments/environment";
import {catchError, map} from "rxjs/operators";

/**
 * Anything regarding Rest calls for changing and retrieving and deleteing Item Definitions.
 */
@Injectable({
  providedIn: 'root'
})
export class ItemdefinitionsRestService implements AdminRestService<ItemDefinition, number> {

  cache$: Observable<ItemDefinition[]> | null = null;

  private definitionsUrl: string;

  constructor(private http: HttpClient,
              private errorsService: ErrorsService,
              private toastService: ToastService) {
    this.definitionsUrl = environment.ITEMDEFINITIONS_URL;
  }

  public get(id: number): Observable<ItemDefinition> {
    return this.http.get<ItemDefinition>(this.definitionsUrl + '/' + id)
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
    this.cache$ = this.http.get<ItemDefinition[]>(this.definitionsUrl)
      .pipe(
        map(items => {
          const newItems = new Array<ItemDefinition>();
          items.forEach(item => newItems.push(new ItemDefinition(item)));
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

  /**
   * Retrieves all items with this itemdefinition.
   */
  public getAllItems(itemdefinitionid: number): Observable<Item[]> {
    return this.http.get<Item[]>(this.definitionsUrl + '/' + itemdefinitionid + '/items')
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

  public clearCache() {
    this.cache$ = null;
  }

  public delete(item: ItemDefinition): Observable<any> {
    return this.http.delete(this.definitionsUrl + '/' + item.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }


  public update(item: ItemDefinition): any {
    // update
    return this.http.put<ItemDefinition[]>(this.definitionsUrl + '/' + item.id, item)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public create(item: ItemDefinition): any {
    // new
    return this.http.post(this.definitionsUrl, item)
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
