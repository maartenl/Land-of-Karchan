import {inject, Injectable} from '@angular/core';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Item, ItemDefinition} from './items/item.model';
import {environment} from '../environments/environment';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {MudCharacter} from './characters/character.model';
import {AdminRestService} from './admin/admin-rest.service';
import {urls} from './urls';

@Injectable({
  providedIn: 'root',
})
export class ItemdefinitionsRestService extends AdminRestService<ItemDefinition, number> {

  url: string = urls.ITEMDEFINITIONS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<ItemDefinition[]> | null = null;

  public get(id: number): Observable<ItemDefinition> {
    return this.http.get<ItemDefinition>(this.url + '/' + id + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all items.", "Loading...");
    this.cache$ = this.http.get<ItemDefinition[]>(this.url + environment.postfix)
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

}
