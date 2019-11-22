import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Mail, MailList } from './mail/mail.model';
import {
  Guild, GuildHopeful, GuildMember, GuildRank,
  GuildHopefuls, GuildMembers, GuildRanks
} from './guild/guild.model';
import { Family } from './player-settings/family.model';
import { Wikipage } from './wikipages/wikipage.model';
import { Picture } from './pictures/picture.model';
import { HasNewMail } from './game/newmail.model';
import { ErrorMessage } from './errors/errormessage.model';

import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  name: string;

  charactersheetUrl: string;

  mailUrl: string;

  hasNewMailUrl: string;

  familyUrl: string;

  guildUrl: string;

  guildhopefulsUrl: string;

  guildmembersUrl: string;

  guildranksUrl: string;

  privateUrl: string;

  wikipagesUrl: string;

  picturesUrl: string;

  wikipagesPreviewUrl: string;

  constructor(
    private cookieService: CookieService,
    private http: HttpClient,
    private errorsService: ErrorsService) {
    this.charactersheetUrl = environment.CHARACTERSHEET_URL;
    this.familyUrl = environment.FAMILY_URL;
    this.mailUrl = environment.MAIL_URL;
    this.hasNewMailUrl = environment.HASNEWMAIL_URL;
    this.guildUrl = environment.GUILD_URL;
    this.guildhopefulsUrl = environment.GUILDHOPEFULS_URL;
    this.guildmembersUrl = environment.GUILDMEMBERS_URL;
    this.guildranksUrl = environment.GUILDRANKS_URL;
    this.wikipagesUrl = environment.WIKIPAGES_URL;
    this.picturesUrl = environment.PICTURES_URL;
    this.privateUrl = environment.PRIVATE_URL;
    this.wikipagesPreviewUrl = environment.WIKIPAGES_PREVIEW_URL;
  }

  /**
   * Retrieves the name of the player from the karchanname cookie.
   */
  public getName(): string {
    if (this.name != null) {
      return this.name;
    }
    if (this.cookieService.check('karchanname')) {
      this.name = this.cookieService.get('karchanname');
      return this.name;
    }
    const error: ErrorMessage = new ErrorMessage();
    error.type = '404';
    error.message = 'Character not found. Are you sure you are logged in?';
    this.errorsService.addError(error);
  }

  public isLoggedIn(): boolean {
    if (this.name != null && this.name !== '') {
      return true;
    }
    return this.cookieService.check('karchanname');
  }

  public isDeputy(): boolean {
    if (!this.cookieService.check('karchanroles')) {
      return false;
    }
    return this.cookieService.get('karchanroles').includes('deputy');
  }

  // get appropriate urls for rest calls

  private getPrivateUrl(): string {
    return this.privateUrl.replace('[player]', this.getName());
  }

  private getCharactersheetUrl(): string {
    return this.charactersheetUrl.replace('[player]', this.getName());
  }

  private getMailUrl(): string {
    return this.mailUrl.replace('[player]', this.getName());
  }

  private getHasNewMailUrl(): string {
    return this.hasNewMailUrl.replace('[player]', this.getName());
  }

  private getFamilyUrl(toname: string): string {
    return this.familyUrl.replace('[player]', this.getName()) + toname;
  }

  private getGuildUrl(): string {
    return this.guildUrl.replace('[player]', this.getName());
  }

  private getGuildmembersUrl(): string {
    return this.guildmembersUrl.replace('[player]', this.getName());
  }

  private getGuildranksUrl(): string {
    return this.guildranksUrl.replace('[player]', this.getName());
  } private getGuildhopefulsUrl(): string {
    return this.guildhopefulsUrl.replace('[player]', this.getName());
  }

  private getWikipagesUrl(title?: string): string {
    if (title === undefined) {
      return this.wikipagesUrl;
    }
    return this.wikipagesUrl + '/' + title;
  }

  private getPicturesUrl(): string {
    return this.picturesUrl.replace('[player]', this.getName());
  }

  // charactersheet calls
  public getPlayer(): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Accept: 'application/json'
      })
    };
    return this.http.get<Player>(this.getCharactersheetUrl(), httpOptions)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updatePlayer(player: Player): Observable<any> {
    return this.http.put(this.getCharactersheetUrl(), player)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  /**
   * Permanently deletes a character, by calling the appropriate rest service.
   */
  public deleteCharacter(): Observable<any> {
    return this.http.delete(this.getPrivateUrl())
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  // mail calls

  public getMail(offset: number): Observable<any> {
    return this.http.get<Mail[]>(this.getMailUrl() + '?offset=' + offset)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public hasNewMail(): Observable<HasNewMail> {
    return this.http.get<HasNewMail>(this.getHasNewMailUrl())
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public sendMail(mail: Mail): Observable<any> {
    return this.http.post(this.getMailUrl(), mail)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public deleteMail(mail: Mail): Observable<any> {
    return this.http.delete(this.getMailUrl() + '/' + mail.id)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  // guild calls

  public getGuild(): Observable<any> {
    return this.http.get<Guild>(this.getGuildUrl())
      .pipe(
        catchError(err => {
          console.log(err);
          this.handleError(err);
          return [];
        })
      );
  }

  public getGuildmembers(): Observable<any> {
    return this.http.get<GuildMember[]>(this.getGuildmembersUrl())
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getGuildranks(): Observable<any> {
    return this.http.get<GuildRank[]>(this.getGuildranksUrl())
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getGuildhopefuls(): Observable<any> {
    return this.http.get<GuildHopeful[]>(this.getGuildhopefulsUrl())
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateGuild(guild: Guild): Observable<any> {
    return this.http.put(this.getGuildUrl(), guild)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateGuildmember(member: GuildMember): Observable<any> {
    return this.http.put(this.getGuildmembersUrl() + '/' + member.name, member)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateGuildrank(rank: GuildRank): Observable<any> {
    return this.http.put(this.getGuildranksUrl() + '/' + rank.guildlevel, rank)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public createGuildrank(rank: GuildRank): Observable<any> {
    return this.http.post(this.getGuildranksUrl(), rank)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public deleteMember(member: GuildMember): Observable<any> {
    return this.http.delete(this.getGuildmembersUrl() + '/' + member.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public deleteRank(rank: GuildRank): Observable<any> {
    return this.http.delete(this.getGuildranksUrl() + '/' + rank.guildlevel)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public deleteHopeful(hopeful: GuildHopeful): Observable<any> {
    return this.http.delete(this.getGuildhopefulsUrl() + '/' + hopeful.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  // family calls

  public deleteFamily(family: Family): Observable<any> {
    return this.http.delete(this.getFamilyUrl(family.toname))
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  private getDescriptionAsInteger(family: Family): string {
    for (const v in Family.FAMILYVALUES) {
      if (family.description === Family.FAMILYVALUES[v]) {
        return v;
      }
    }
    const errormessage = new ErrorMessage();
    errormessage.message = 'Could not translate familyrelation.';
    errormessage.type = 'Logic Error';
    this.errorsService.addError(errormessage);
    throw new Error('Could not translate familyrelation.');
  }

  public updateFamily(family: Family): Observable<any> {
    const url: string = this.getFamilyUrl(family.toname) + '/' + this.getDescriptionAsInteger(family);
    return this.http.put(url, null)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  // wikipages

  public getWikipagePreview(contents: string): Observable<string> {
    if (!environment.production) {
      return new Observable(observer => {
        observer.next('<p>This is a <i>preview</i></p>');
        observer.complete();
      });
    }
    const headers = new HttpHeaders()
      .append('Content-Type', 'text/html; charset=utf-8')
      .append('Accept', 'text/html');
    return this.http.post(this.wikipagesPreviewUrl, contents, { responseType: 'text' , headers })
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getWikipage(title: string): Observable<Wikipage> {
    return this.http.get<Wikipage>(this.getWikipagesUrl(title))
      .pipe(
        catchError(err => {
          this.handleError(err, ['404']);
          return [];
        })
      );
  }

  public createWikipage(wikipage: Wikipage): Observable<any> {
    return this.http.post(this.getWikipagesUrl(), wikipage)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public updateWikipage(wikipage: Wikipage): Observable<any> {
    return this.http.put(this.getWikipagesUrl(wikipage.title), wikipage)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  // pictures

  public getPictures(): Observable<any> {
    return this.http.get<Picture[]>(this.getPicturesUrl())
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public createPicture(picture: Picture): Observable<any> {
    return this.http.post(this.getPicturesUrl(), picture)
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
