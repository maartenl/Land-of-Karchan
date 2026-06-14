import {inject, Injectable} from '@angular/core';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Command} from './commands/command.model';
import {AdminRestService} from './admin/admin-rest.service';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CommandsRestService extends AdminRestService<Command, number> {
  url: string = urls.COMMANDS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Command[]> | null = null;


  public get(id: number): Observable<Command> {
    return this.http.get<Command>(this.url + '/' + id + environment.postfix)
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
    this.toastService.showRetrieving("Retrieving all commands.", "Loading...");
    this.cache$ = this.http.get<Command[]>(this.url + environment.postfix)
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
}
