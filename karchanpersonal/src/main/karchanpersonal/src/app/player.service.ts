import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Mail, MailList } from './mail/mail.model';
import {
  Guild, GuildHopeful, GuildMember, GuildRank,
  GuildHopefuls, GuildMembers, GuildRanks
} from './guild/guild.model';
import { Family } from './player-settings/family.model';
import { Error } from './errors/error.model';


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

  gameUrl: string;

  privateUrl: string;

  constructor(private http: HttpClient, private errorsService: ErrorsService) {
    this.charactersheetUrl = environment.CHARACTERSHEET_URL;
    this.familyUrl = environment.FAMILY_URL;
    this.gameUrl = environment.GAME_URL;
    this.mailUrl = environment.MAIL_URL;
    this.hasNewMailUrl = environment.HASNEWMAIL_URL;
    this.guildUrl = environment.GUILD_URL;
    this.guildhopefulsUrl = environment.GUILDHOPEFULS_URL;
    this.guildmembersUrl = environment.GUILDMEMBERS_URL;
    this.guildranksUrl = environment.GUILDRANKS_URL;
    this.privateUrl = environment.PRIVATE_URL;
  }

  /**
   * Retrieves the name of the player from the local storage.
   */
  public getName(): string {
    if (this.name != null) {
      return this.name;
    }
    if (typeof (Storage) !== 'undefined') {
      // Store
      this.name = localStorage.getItem('karchanname');
      if (this.name !== null) {
        return this.name;
      }
    }
    const error: Error = new Error();
    error.type = '404';
    error.message = 'Character not found. Are you sure you are logged in?';
    this.errorsService.addError(error);
  }

  public isLoggedIn(): boolean {
    if (this.name != null) {
      return true;
    }
    if (typeof (Storage) !== 'undefined') {
      // Store
      this.name = localStorage.getItem('karchanname');
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
    if (typeof (Storage) !== 'undefined') {
      // Store
      localStorage.removeItem('karchanname');
    }
    this.name = null;
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
  }

  private getGuildhopefulsUrl(): string {
    return this.guildhopefulsUrl.replace('[player]', this.getName());
  }

  private getGameUrl(): string {
    return this.gameUrl.replace('[player]', this.getName());
  }

  // charactersheet calls

  public getPlayer(): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
      })
    };
    return this.http.get<Player>(this.getCharactersheetUrl(), httpOptions);
  }

  public updatePlayer(player: Player): Observable<any> {
    return this.http.put(this.getCharactersheetUrl(), player);
  }

  /**
   * Permanently deletes a character, by calling the appropriate rest service.
   */
  public deleteCharacter(): Observable<any> {
    return this.http.delete(this.getPrivateUrl());
  }

  // mail calls

  public getMail(offset: number): Observable<any> {
    return this.http.get<Mail[]>(this.getMailUrl() + '?offset=' + offset);
  }

  public hasNewMail(): Observable<any> {
    return this.http.get(this.getHasNewMailUrl());
  }

  public sendMail(mail: Mail): Observable<any> {
    return this.http.post(this.getMailUrl(), mail);
  }

  public deleteMail(mail: Mail): Observable<any> {
    return this.http.delete(this.getMailUrl() + '/' + mail.id);
  }

  // guild calls

  public getGuild(): Observable<any> {
    return this.http.get<Guild>(this.getGuildUrl());
  }

  public getGuildmembers(): Observable<any> {
    return this.http.get<GuildMember[]>(this.getGuildmembersUrl());
  }

  public getGuildranks(): Observable<any> {
    return this.http.get<GuildRank[]>(this.getGuildranksUrl());
  }

  public getGuildhopefuls(): Observable<any> {
    return this.http.get<GuildHopeful[]>(this.getGuildhopefulsUrl());
  }

  public updateGuild(guild: Guild): Observable<any> {
    return this.http.put(this.getGuildUrl(), guild);
  }

  public updateGuildmember(member: GuildMember): Observable<any> {
    return this.http.put(this.getGuildmembersUrl() + '/' + member.name, member);
  }

  public updateGuildrank(rank: GuildRank): Observable<any> {
    return this.http.put(this.getGuildranksUrl() + '/' + rank.guildlevel, rank);
  }

  public createGuildrank(rank: GuildRank): Observable<any> {
    return this.http.post(this.getGuildranksUrl(), rank);
  }

  public deleteMember(member: GuildMember): Observable<any> {
    return this.http.delete(this.getGuildmembersUrl() + '/' + member.name);
  }

  public deleteRank(rank: GuildRank): Observable<any> {
    return this.http.delete(this.getGuildranksUrl() + '/' + rank.guildlevel);
  }

  public deleteHopeful(hopeful: GuildHopeful): Observable<any> {
    return this.http.delete(this.getGuildhopefulsUrl() + '/' + hopeful.name);
  }

  // family calls

  public deleteFamily(family: Family): Observable<any> {
    return this.http.delete(this.getFamilyUrl(family.toname));
  }

  public updateFamily(family: Family): Observable<any> {
    const url: string = this.getFamilyUrl(family.toname) + '/' + family.getDescriptionAsInteger();
    return this.http.put(url, null);
  }

  // game calls

  public enterGame(): Observable<any> {
    const url: string = this.getGameUrl() + 'enter';
    return this.http.post(url, null);
  }

  public logoff(): Observable<any> {
    const url: string = this.getGameUrl() + 'logoff';
    return this.http.put(url, null);
  }

}
