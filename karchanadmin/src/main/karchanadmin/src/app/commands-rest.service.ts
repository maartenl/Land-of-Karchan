import {Observable, ReplaySubject, share} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {environment} from '../environments/environment';

import {ErrorsService} from './errors.service';
import {Command} from './commands/command.model';
import {AdminRestService} from './admin/admin-rest.service';
import {ToastService} from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class CommandsRestService implements AdminRestService<Command, number> {
  url: string;

  cache$: Observable<Command[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = environment.COMMANDS_URL;
  }

  public get(id: number): Observable<Command> {
    return this.http.get<Command>(this.url + '/' + id)
      .pipe(
        map(item => new Command(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getAll(): Observable<Command[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all commands.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<Command[]>(this.url)
      .pipe(
        map(items => {
          const newItems = new Array<Command>();
          items.forEach(item => newItems.push(new Command(item)));
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

  public delete(command: Command): Observable<any> {
    return this.http.delete(this.url + '/' + command.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(command: Command): any {
    // update
    return this.http.put<Command[]>(this.url + '/' + command.id, command)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public create(command: Command): any {
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
