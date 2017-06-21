import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

import { environment } from 'app/../environments/environment';

import { deserialize, serialize, JsonProperty } from 'json-typescript-mapper';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Mail, MailList } from './mail/mail.model';
import { Guild, GuildHopeful, GuildMember, GuildRank, GuildHopefuls, GuildMembers, GuildRanks } from './guild/guild.model';
import { Family } from './player-settings/family.model';
import { Error } from './errors/error.model';

@Injectable()
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

  gameUrl: string;

  constructor(private http: Http, private errorsService: ErrorsService) {
    this.charactersheetUrl = environment.CHARACTERSHEET_URL;
    this.familyUrl = environment.FAMILY_URL;
    this.gameUrl = environment.GAME_URL;
    this.mailUrl = environment.MAIL_URL;
    this.hasNewMailUrl = environment.HASNEWMAIL_URL;
    this.guildUrl = environment.GUILD_URL;
    this.guildhopefulsUrl = environment.GUILDHOPEFULS_URL;
    this.guildmembersUrl = environment.GUILDMEMBERS_URL;
    this.guildranksUrl = environment.GUILDRANKS_URL;
  }

  /**
   * Retrieves the name of the player from the local storage.
   */
  public getName(): string {
    if (this.name != null) {
      return this.name;
    }
    if (typeof (Storage) !== "undefined") {
      // Store
      this.name = localStorage.getItem("karchanname");
      if (this.name !== null) {
        return this.name;
      }
    }
    let error: Error = new Error();
    error.type = "404";
    error.message = "Character not found. Are you sure you are logged in?";
    this.errorsService.addError(error);
  }

  public isLoggedIn(): boolean {
    if (this.name != null) {
      return true;
    }
    if (typeof (Storage) !== "undefined") {
      // Store
      this.name = localStorage.getItem("karchanname");
      if (this.name !== null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Clears the name of the player from the local storage.
   */
  public clearName(): void {
    if (typeof (Storage) !== "undefined") {
      // Store
      localStorage.removeItem("karchanname");
    }
    this.name = null;
  }

  private getCharactersheetUrl(): string {
    return this.charactersheetUrl.replace("[player]", this.getName());
  }

  private getMailUrl(): string {
    return this.mailUrl.replace("[player]", this.getName());
  }

  private getHasNewMailUrl(): string {
    return this.hasNewMailUrl.replace("[player]", this.getName());
  }

  private getFamilyUrl(toname: string): string {
    return this.familyUrl.replace("[player]", this.getName()) + toname;
  }

  private getGuildUrl(): string {
    return this.guildUrl.replace("[player]", this.getName());
  }

  private getGuildmembersUrl(): string {
    return this.guildmembersUrl.replace("[player]", this.getName());
  }

  private getGuildranksUrl(): string {
    return this.guildranksUrl.replace("[player]", this.getName());
  }

  private getGuildhopefulsUrl(): string {
    return this.guildhopefulsUrl.replace("[player]", this.getName());
  }

  private getGameUrl(): string {
    return this.gameUrl.replace("[player]", this.getName());
  }

  // charactersheet calls

  public getPlayer(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getCharactersheetUrl(), options)
      .map(this.extractData)
      .catch((n) => this.handleError(n));
  }

  public updatePlayer(player: Player): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.put(this.getCharactersheetUrl(), player, options)
      .catch((n) => this.handleError(n));
  }

  // mail calls

  public getMail(offset: number): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getMailUrl() + "?offset=" + offset, options)
      .map(this.extractMail)
      .catch((n) => this.handleError(n));
  }

  public hasNewMail(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getHasNewMailUrl(), options)
      .map((res) => res.status === 200);
  }

  public sendMail(mail: Mail): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(this.getMailUrl(), mail, options)
      .catch((n) => this.handleError(n));
  }

  public deleteMail(mail: Mail): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.delete(this.getMailUrl() + "/" + mail.id, options)
      .catch((n) => this.handleError(n));
  }

  // guild calls

  public getGuild(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getGuildUrl(), options)
      .map(this.extractGuild)
      .catch((n) => this.handleError(n));
  }

  public getGuildmembers(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getGuildmembersUrl(), options)
      .map(this.extractGuildmembers)
      .catch((n) => this.handleError(n));
  }

  public getGuildranks(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getGuildranksUrl(), options)
      .map(this.extractGuildranks)
      .catch((n) => this.handleError(n));
  }

  public getGuildhopefuls(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.get(this.getGuildhopefulsUrl(), options)
      .map(this.extractGuildhopefuls)
      .catch((n) => this.handleError(n));
  }

  public updateGuild(guild: Guild): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.put(this.getGuildUrl(), guild, options)
      .catch((n) => this.handleError(n));
  }

  public updateGuildmember(member: GuildMember): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.put(this.getGuildmembersUrl() + '/' + member.name, member, options)
      .catch((n) => this.handleError(n));
  }

  public deleteMember(member: GuildMember): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.delete(this.getGuildmembersUrl() + "/" + member.name, options)
      .catch((n) => this.handleError(n));
  }

  public deleteRank(rank: GuildRank): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.delete(this.getGuildranksUrl() + "/" + rank.guildlevel, options)
      .catch((n) => this.handleError(n));
  }

  public deleteHopeful(hopeful: GuildHopeful): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    return this.http.delete(this.getGuildhopefulsUrl() + "/" + hopeful.name, options)
      .catch((n) => this.handleError(n));
  }
  // family calls

  public deleteFamily(family: Family): Observable<any> {
    return this.http.delete(this.getFamilyUrl(family.toname))
      .catch((n) => this.handleError(n));
  }

  public updateFamily(family: Family): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    let url: string = this.getFamilyUrl(family.toname) + "/" + family.getDescriptionAsInteger();
    return this.http.put(url, null, options)
      .catch((n) => this.handleError(n));
  }

  // game calls

  public enterGame(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    let url: string = this.getGameUrl() + "enter";
    return this.http.post(url, null, options)
      .catch((n) => this.handleError(n));
  }

  public logoff(): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    let url: string = this.getGameUrl() + "logoff";
    return this.http.put(url, null, options)
      .catch((n) => this.handleError(n));
  }

  private extractData(res: Response): Player {
    let body = res.json();
    const player = deserialize(Player, body);
    return player || new Player();
  }

  private extractMail(res: Response): MailList {
    let body = res.json();
    const mails = deserialize(MailList, { mails: body });
    return mails;
  }

  private extractGuild(res: Response): Guild {
    let body = res.json();
    const guild = deserialize(Guild, body);
    return guild;
  }

  private extractGuildmembers(res: Response): GuildMembers {
    let body = res.json();
    const members = deserialize(GuildMembers, { members: body });
    return members;
  }

  private extractGuildranks(res: Response): GuildRanks {
    let body = res.json();
    const ranks = deserialize(GuildRanks, { ranks: body });
    return ranks;
  }

  private extractGuildhopefuls(res: Response): GuildHopefuls {
    let body = res.json();
    const hopefuls = deserialize(GuildHopefuls, { hopefuls: body });
    return hopefuls;
  }

  private handleError(response: Response | any): Observable<{}> {
    let error: Error = new Error();
    let errMsg: string;
    if (response instanceof Response) {
      try {
        const body = response.json() || '';
        error.type = response.status + ":" + (response.statusText || '');
        if (body !== '' && body.errormessage) {
          error.message = body.errormessage;
          error.detailedmessage = body.stacktrace;
        } else {
          error.message = JSON.stringify(body);
        }
      } catch (e) {
        // cannot json-parse
        error.type = response.status + "";
        error.message = response.statusText || '';
      }
    } else {
      errMsg = response.message ? response.message : response.toString();
      error.message = errMsg;
    }
    this.errorsService.addError(error);
    return Observable.throw(errMsg);
  }
}
