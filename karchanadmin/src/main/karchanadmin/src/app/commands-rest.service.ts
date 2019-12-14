import { Observable, of, from } from 'rxjs';
import { catchError, publishReplay, refCount } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Command } from './commands/command.model';

@Injectable({
  providedIn: 'root'
})
export class CommandsRestService {
  url: string;

  cache$: Observable<Command[]>;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.url = environment.COMMANDS_URL;
  }

  public getCount(owner: string): Observable<number> {
    const localUrl = owner === null || owner === undefined ? this.url + '/count' : this.url + '/count' + '?owner=' + owner;
    return this.http.get<Command[]>(localUrl)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommand(id: string): Observable<Command> {
    return this.http.get<Command>(this.url + '/' + id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getCommands(): Observable<Command[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.cache$ = this.http.get<Command[]>(this.url)
      .pipe(
        publishReplay(1),
        refCount(),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
    return this.cache$;
  }

  public clearCache() {
    this.cache$ = undefined;
  }

  public deleteCommand(command: Command): Observable<any> {
    return this.http.delete(this.url + '/' + command.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateCommand(command: Command): any {
    // update
    return this.http.put<Command[]>(this.url + '/' + command.id, command)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public createCommand(command: Command): any {
    // new
    return this.http.post(this.url, command)
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
