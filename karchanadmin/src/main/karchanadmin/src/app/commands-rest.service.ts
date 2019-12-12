import { Observable, of, from } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, map, tap } from 'rxjs/operators';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Command } from './commands/command.model';

@Injectable({
  providedIn: 'root'
})
export class CommandsRestService {
  url: string;

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

  public getCommands(startRow: number, endRow: number, owner: string): Observable<Command[]> {
    const localUrl = owner === null || owner === undefined ? this.url + '/' + startRow + '/' + (endRow - startRow) :
      this.url + '/' + startRow + '/' + (endRow - startRow) + '?owner=' + owner;
    return this.http.get<Command[]>(localUrl)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
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
