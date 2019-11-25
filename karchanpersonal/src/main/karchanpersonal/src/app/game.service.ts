import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { PlayerService } from './player.service';
import { ErrorMessage } from './errors/errormessage.model';
import { Display } from './play/display.model';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  gameUrl: string;

  constructor(
    private cookieService: CookieService,
    private playerService: PlayerService,
    private http: HttpClient,
    private errorsService: ErrorsService) {
    this.gameUrl = environment.GAME_URL;
  }

  private getGameUrl(): string {
    return this.gameUrl.replace('[player]', this.playerService.getName());
  }

  // game calls

  public enterGame(): Observable<any> {
    if (!environment.production) {
      return this.http.get(this.getGameUrl() + 'enter.json');
    }
    const url: string = this.getGameUrl() + 'enter';
    return this.http.post(url, null);
  }

  public quitGame(): Observable<any> {
    const url: string = this.getGameUrl() + 'quit';
    return this.http.get(url);
  }

  public processCommand(command: string, logOffset: number, log: boolean): Observable<Display> {
    if (!environment.production) {
      return this.http.get<Display>(this.getGameUrl() + 'play.json');
    }
    const url: string = this.getGameUrl() + 'play?offset=' + logOffset + + '&log=' + log;
    return this.http.post<Display>(url, command);
  }

  /**
   * Handles error, delivers them to the errorService.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  private handleError(error: HttpErrorResponse, ignore?: string[]) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.message;
      errormessage.type = 'Network Error';
      this.errorsService.addError(errormessage);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      if (ignore !== undefined && ignore.includes(error.status.toString())) {
        return;
      }
      const errormessage = new ErrorMessage();
      errormessage.message = error.error.errormessage;
      if (error.error.errormessage === undefined) {
        errormessage.message = error.statusText;
      }
      errormessage.type = error.status.toString();
      this.errorsService.addError(errormessage);
    }
  }

}
