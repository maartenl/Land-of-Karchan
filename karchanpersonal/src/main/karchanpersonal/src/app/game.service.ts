import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {ErrorsService} from './errors.service';
import {environment} from '../environments/environment';
import {Log} from './play/log.model';
import {Observable} from 'rxjs';
import {Display} from './play/display.model';
import {PlayerService} from './player.service';
import {Logonmessage} from './play/logonmessages/logonmessage.model';
import {urls} from './urls';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private http = inject(HttpClient);
  private cookieService = inject(CookieService)
  private errorsService= inject(ErrorsService)
  private playerService= inject(PlayerService)

  gameUrl: string;

  whoList: string;

  showLogonmessage = true;

  isGaming: boolean;

  constructor() {
    this.gameUrl = urls.GAME_URL;
    this.whoList = urls.WHO_URL;
    this.isGaming = false;
  }


  private getGameUrl(): string {
    return this.gameUrl.replace('[player]', this.playerService.getName());
  }

  // game calls

  public enterGame(): Observable<any> {
    const url: string = this.getGameUrl() + 'enter' + environment.postfix;
    return this.http.post(url, null);
  }

  getLogonmessage(): Observable<Logonmessage> {
    const url: string = this.getGameUrl() + 'logonmessage' + environment.postfix;;
    return this.http.get<Logonmessage>(url);
  }

  getWho(): Observable<any> {
    const url: string = this.whoList;
    return this.http.get(url + environment.postfix);
  }

  public setIsGaming(isGaming: boolean) {
    this.isGaming = isGaming;
  }

  public getIsGaming(): boolean {
    return this.isGaming;
  }

  public getShowLogonmessage(): boolean {
    return this.showLogonmessage;
  }

  public setShowLogonmessage(show: boolean) {
    this.showLogonmessage = show;
  }

  public quitGame(): Observable<any> {
    const url: string = this.getGameUrl() + 'quit' + environment.postfix;
    return this.http.get(url);
  }

  /**
   * Run a command on the server.
   * @param command the command to execute
   * @param offset the offset in the log
   * @param log wether or not a log in the answer from the server is required, if false then no log is sent.
   * @returns a Display
   */
  public processCommand(command: string, offset: number | null | undefined, log: boolean): Observable<Display> {
    const params = log ? ("?offset=" + offset + "&log=true") : "";
    const url: string = this.getGameUrl() + 'play' + environment.postfix + params;
    return this.http.post<Display>(url, command);
  }

  public getLog(): Observable<Log> {
    const url: string = this.getGameUrl() + 'log' + environment.postfix;
    return this.http.get<Log>(url);
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
