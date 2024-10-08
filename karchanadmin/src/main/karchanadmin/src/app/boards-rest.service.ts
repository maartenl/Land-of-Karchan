import {Injectable} from '@angular/core';
import {Board, BoardMessage} from './boards/board.model';
import {Observable, ReplaySubject, share} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';

import {ErrorsService} from './errors.service';
import {AdminRestService} from './admin/admin-rest.service';
import {ToastService} from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class BoardsRestService implements AdminRestService<Board, number> {
  url: string;

  cache$: Observable<Board[]> | null = null;

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService,
    private toastService: ToastService) {
    this.url = '/karchangame/resources/administration/boards';
  }

  get(id: number): Observable<Board> {
    return this.http.get<Board>(this.url + '/' + id)
      .pipe(
        map(item => new Board(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<Board[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.show('Retrieving all boards.', {
      delay: 5000,
      autohide: true,
      headertext: 'Loading...'
    });
    this.cache$ = this.http.get<Board[]>(this.url)
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

  getMessages(boardid: number) {
    return this.http.get<BoardMessage[]>(this.url + '/' + boardid + '/messages')
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

  clearCache() {
    this.cache$ = null;
  }

  delete(board: Board): Observable<any> {
    return this.http.delete(this.url + '/' + board.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(board: Board) {
    // update
    return this.http.put<Board[]>(this.url + '/' + board.id, board)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(board: Board) {
    // new
    return this.http.post(this.url, board)
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
