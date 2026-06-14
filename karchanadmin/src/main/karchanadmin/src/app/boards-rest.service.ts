import {inject, Injectable} from '@angular/core';
import {AdminRestService} from './admin/admin-rest.service';
import {urls} from './urls';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {environment} from '../environments/environment';
import {Command} from './commands/command.model';
import {Board, BoardMessage} from './boards/board.model';

@Injectable({
  providedIn: 'root',
})
export class BoardsRestService extends AdminRestService<Board, number> {
  url: string = urls.BOARDS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Board[]> | null = null;

  public get(id: number): Observable<Board> {
    return this.http.get<Board>(this.url + '/' + id + environment.postfix)
      .pipe(
        map(item => new Board(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public clearCache() {
    this.cache$ = null;
  }

  public getAll(): Observable<Board[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all Boards.", "Loading...");
    this.cache$ = this.http.get<Board[]>(this.url+ environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Board>();
          items.forEach(item => newItems.push(new Board(item)));
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

  public delete(board: Board): Observable<any> {
    return this.http.delete(this.url + '/' + board.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public update(board: Board): any {
    if (board.name !== undefined) {
      // update
      return this.http.put<Board[]>(this.url + '/' + board.id, board)
        .pipe(
          catchError(err => {
            this.handleError(err);
            return [];
          })
        );
    }
  }

  public create(board: Board): any {
    // new
    return this.http.post(this.url, board)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getMessages(boardid: number) {
    return this.http.get<BoardMessage[]>(this.url + '/' + boardid + '/messages' + environment.postfix)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  updateMessage(message: BoardMessage) {
    // update
    return this.http.put<BoardMessage>(this.url + '/' + message.boardid + '/messages/' + message.id, message)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

}
