import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

import { environment } from 'app/../environments/environment';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Family } from './player-settings/family.model';
import { Error } from './errors/error.model';

@Injectable()
export class PlayerService {
  name: string;

  charactersheetUrl: string;

  familyUrl: string;

  constructor(private http: Http, private errorsService: ErrorsService) {
    this.charactersheetUrl = environment.CHARACTERSHEET_URL;
    this.familyUrl = environment.FAMILY_URL;
  }

  /**
   * Retrieves the name of the player either from a cookie
   * or from the internal storage.
   */
  public getName(): string {
    if (this.name != null) {
      console.log("cachednbame ", this.name);
      return this.name;
    }
    if (typeof (Storage) !== "undefined") {
      // Store
      this.name = localStorage.getItem("karchanname");
      console.log("localstorage ", this.name);
      return this.name;
    }
    //  else {
    //     return Cookies.get('karchanname');
    // }
    let error: Error = new Error();
    error.type = "404";
    error.message = "Player not found.";
    error.detailedmessage = "I looked in my cookies and my internal storage, but could not find the name of the player. Are you sure you are logged in?";
    this.errorsService.addError(error);
  }

  private getCharactersheetUrl(): string {
    return this.charactersheetUrl.replace("[player]", this.getName());
  }

  private getFamilyUrl(toname: string): string {
    return this.familyUrl.replace("[player]", this.getName()) + toname;
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

  public deleteFamily(player: Player, family: Family): Observable<any> {
    return this.http.delete(this.getFamilyUrl(family.toname))
      .catch((n) => this.handleError(n));
  }

  public updateFamily(player: Player, family: Family): Observable<any> {
    let headers = new Headers({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
    let options = new RequestOptions({ headers: headers });
    let url: string = this.getFamilyUrl(family.toname) + "/" + family.getDescriptionAsInteger();
    return this.http.put(url, null, options)
      .catch((n) => this.handleError(n));
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || {};
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