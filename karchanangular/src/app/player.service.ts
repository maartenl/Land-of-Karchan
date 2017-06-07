import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

import { environment } from 'app/../environments/environment';

import { deserialize, serialize, JsonProperty } from 'json-typescript-mapper';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Family } from './player-settings/family.model';
import { Error } from './errors/error.model';

class Student {
  @JsonProperty('name')
  fullName: string;

  constructor() {
    this.fullName = undefined;
  }
}

class Address {
  @JsonProperty('first-line')
  firstLine: string;
  @JsonProperty('second-line')
  secondLine: string;
  @JsonProperty({ clazz: Student })
  student: Student;
  city: string;

  constructor() {
    this.firstLine = undefined;
    this.secondLine = undefined;
    this.city = undefined;
    this.student = undefined
  }
}

class Person {
  @JsonProperty('Name')
  name: string;
  @JsonProperty('xing')
  surname: string;
  age: number;
  @JsonProperty({ clazz: Address, name: 'AddressArr' })
  addressArr: Address[];
  @JsonProperty({ clazz: Address, name: 'Address' })
  address: Address;

  constructor() {
    this.name = void 0;
    this.surname = void 0;
    this.age = void 0;
    this.addressArr = void 0;
    this.address = void 0;
  }
}

@Injectable()
export class PlayerService {
  name: string;

  charactersheetUrl: string;

  familyUrl: string;

  gameUrl: string;

  constructor(private http: Http, private errorsService: ErrorsService) {
    this.charactersheetUrl = environment.CHARACTERSHEET_URL;
    this.familyUrl = environment.FAMILY_URL;
    this.gameUrl = environment.GAME_URL;
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

  private getFamilyUrl(toname: string): string {
    return this.familyUrl.replace("[player]", this.getName()) + toname;
  }

  private getGameUrl(): string {
    return this.gameUrl.replace("[player]", this.getName());
  }

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

  private extractData(res: Response) {
    let body = res.json();
    const player = deserialize(Player, body);
    return player || new Player();
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