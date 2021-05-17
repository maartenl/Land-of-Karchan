import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {MudCharacter} from './characters/character.model';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {Attribute} from './attribute.model';

@Injectable({
  providedIn: 'root'
})
export class AttributesRestService {
  url: string;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService) {
    this.url = environment.ATTRIBUTES_URL;
  }

  public getFromCharacter(character: MudCharacter): Observable<Attribute[]> {
    return this.http.get<Attribute[]>(this.url + '/byType/PERSON/' + character.name)
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

  public getAll(name: string): Observable<Attribute[]> {
    return this.http.get<Attribute[]>(this.url + '/byName/' + name)
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


  delete(attribute: Attribute): Observable<any> {
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
  update(attribute: Attribute): Observable<any> {
    // update
    return this.http.put<Attribute>(this.url + '/byType/' + attribute.objecttype + '/' + attribute.objectid, attribute)
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
