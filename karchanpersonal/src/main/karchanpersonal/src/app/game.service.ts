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
    this.errorsService.addHttpError(error, ignore);
  }
}
