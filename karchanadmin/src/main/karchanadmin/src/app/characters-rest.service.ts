import { Injectable } from '@angular/core';
import { MudCharacter } from './characters/character.model';
import {Observable, ReplaySubject, share} from 'rxjs';
import { catchError, publishReplay, refCount, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { AdminRestService } from './admin/admin-rest.service';
import { ToastService } from './toast.service';
import {Item} from "./items/item.model";

@Injectable({
  providedIn: 'root'
})
export class CharactersRestService implements AdminRestService<MudCharacter, string>   {

  url: string;

  cache$: Observable<MudCharacter[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.CHARACTERS_URL;
  }

  get(name: string): Observable<MudCharacter> {
    return this.http.get<MudCharacter>(this.url + '/' + name)
      .pipe(
        map(item => new MudCharacter(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<MudCharacter[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all characters.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<MudCharacter[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<MudCharacter>();
          items.forEach(item => newItems.push(new MudCharacter(item)));
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

  clearCache() {
    this.cache$ = null;
  }

  delete(character: MudCharacter): Observable<any> {
    return this.http.delete(this.url + '/' + character.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(character: MudCharacter) {
    // update
    return this.http.put<MudCharacter[]>(this.url + '/' + character.name, character)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(character: MudCharacter) {
    // new
    return this.http.post(this.url, character)
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
  public getAllItems(name: String): Observable<Item[]> {
    return this.http.get<Item[]>(this.url + '/' + name + '/items')
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
