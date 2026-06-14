import {inject, Injectable} from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Attribute} from './attribute.model';
import {Item} from './items/item.model';
import {urls} from './urls';
import {AdminRestService} from './admin/admin-rest.service';

@Injectable({
  providedIn: 'root',
})
export class AttributesRestService extends AdminRestService<Attribute, number> {

  url: string = urls.ATTRIBUTES_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Attribute[]> | null = null;

  public getFromCharacter(name: string): Observable<Attribute[]> {
    this.toastService.showRetrieving("Retrieving all attributes.", "Loading...");
    return this.http.get<Attribute[]>(this.url + '/byType/PERSON/' + name + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Attribute>();
          items.forEach(item => newItems.push(new Attribute(item)));
          return newItems;
        }),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getFromRoom(roomid: number): Observable<Attribute[]> {
    this.toastService.showRetrieving("Retrieving all attributes.", "Loading...");
    return this.http.get<Attribute[]>(this.url + '/byType/ROOM/' + roomid + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Attribute>();
          items.forEach(item => newItems.push(new Attribute(item)));
          return newItems;
        }),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getFromItem(item: Item): Observable<Attribute[]> {
    this.toastService.showRetrieving("Retrieving all attributes.", "Loading...");
    return this.http.get<Attribute[]>(this.url + '/byType/ITEM/' + item.id + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Attribute>();
          items.forEach(item => newItems.push(new Attribute(item)));
          return newItems;
        }),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public override getAll(): Observable<Attribute[]> {
    // method not used in this case, see getAllWithName()
    throw new Error("Method not implemented.");
  }

  public getAllWithName(name: string): Observable<Attribute[]> {
    this.toastService.showRetrieving("Retrieving all attributes.", "Loading...");
    this.cache$ = this.http.get<Attribute[]>(this.url + '/byName/' + name + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Attribute>();
          items.forEach(item => newItems.push(new Attribute(item)));
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

  override delete(attribute: Attribute): Observable<any> {
    return this.http.delete(this.url + '/byId/' + attribute.objecttype + '/' + attribute.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  /**
   * Will create a new one, if the old one doesn't exist.
   * @param attribute the new/updated attribute
   * @returns some stuff
   */
  override update(attribute: Attribute): any {
    // update
    return this.http.put<Attribute>(this.url + '/byType/' + attribute.objecttype + '/' + attribute.objectid, attribute)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  override create(item: Attribute): any {
    return this.update(item);
  }

  override clearCache() {
    this.cache$ = null;
  }

  override get(id: number): Observable<Attribute> {
    return this.cache$?.pipe(map(items => items.filter(item => item.id == id)[0]))
      ?? new Observable<Attribute>(() => {
        new Attribute();
      });
  }

}
