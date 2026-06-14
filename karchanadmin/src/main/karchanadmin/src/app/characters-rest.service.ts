import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {MudCharacter} from './characters/character.model';
import {catchError, map, Observable, ReplaySubject, share} from "rxjs";
import {environment} from '../environments/environment';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {Item} from './items/item.model';
import {urls} from './urls';

@Injectable({
  providedIn: 'root',
})
export class CharactersRestService extends AdminRestService<MudCharacter, string> {

  url: string = urls.CHARACTERS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<MudCharacter[]> | null = null;

  get(name: string): Observable<MudCharacter> {
    return this.http.get<MudCharacter>(this.url + '/' + name + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all characters.", "Loading...");
    this.cache$ = this.http.get<MudCharacter[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<MudCharacter>();
          items.forEach(item => newItems.push(new MudCharacter(item)));
          this.toastService.showMessage("All characters retrieved.", "Done...");
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

  clearCache(): void {
    this.cache$ = null;
  }

  delete(item: MudCharacter): Observable<any> {
    return this.http.delete(this.url + '/' + item.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(item: MudCharacter) {
    // update
    return this.http.put<MudCharacter[]>(this.url + '/' + item.name, item)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(item: MudCharacter) {
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
