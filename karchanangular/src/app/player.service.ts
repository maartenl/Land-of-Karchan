import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

import { environment } from 'app/../environments/environment';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Family } from './player-settings/family.model';
import { Error } from './errors/error.model';

@Injectable()
export class PlayerService {
  url: string;

  updateUrl: string;

  familyUrl: string;

  constructor(private http: Http, private errorsService: ErrorsService) { 
    this.url = environment.CHARACTERSHEET_URL;
    this.updateUrl = environment.UPDATE_CHARACTERSHEET_URL;
    this.familyUrl = environment.FAMILY_URL;
  }

  public getPlayer() : Observable<any> {
    return this.http.get(this.url)
                    .map(this.extractData)
                    .catch((n) => this.handleError(n));
  }

  public updatePlayer(player: Player) : Observable<any> {
    return this.http.put(this.updateUrl, player)
                        .catch((n) => this.handleError(n));
  }

  public deleteFamily(player: Player, family: Family): Observable<any> {
    return this.http.delete(this.familyUrl + family.toname)
                        .catch((n) => this.handleError(n));
  }

  public updateFamily(player: Player, family: Family): Observable<any> {
    return this.http.put(this.familyUrl + family.toname + "/" + family.description, null)
                        .catch((n) => this.handleError(n));
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || { };
  }

  private handleError (response: Response | any):Observable<{}> {
    let error : Error = new Error();
    let errMsg: string;
    if (response instanceof Response) {
      error.type = response.status + "";
      error.message = response.statusText || '';
    } else {
      errMsg = response.message ? response.message : response.toString();
      error.message = errMsg;
    }
    this.errorsService.addError(error);
    return Observable.throw(errMsg);
  }
}