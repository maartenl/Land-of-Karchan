import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

import { environment } from 'app/../environments/environment';

import { ErrorsService } from './errors.service';
import { Player } from './player-settings/player.model';
import { Error } from './errors/error.model';

@Injectable()
export class PlayerService {
  url: string;

  constructor(private http: Http, private errorsService: ErrorsService) { 
    console.log("Constructor:", errorsService);
    this.url = environment.CHARACTERSHEET_URL;
  }

  public getPlayer() : Observable<any> {
    let me = this;
    let func = function(response: Response | any):Observable<{}> {
      me.handleError(response);
      return null;
    };
    console.log("getPlayer: ", this.errorsService);
    return this.http.get(this.url)
                    .map(this.extractData)
                    .catch(func);
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || { };
  }

  private handleError (response: Response | any) {
    console.log("PlayerService.handleError", this);
    console.log(response);
    let error : Error = new Error();
    let errMsg: string;
    if (response instanceof Response) {
      console.log("PlayerService -> Response");
      console.log(response);
      error.type = response.status + "";
      error.message = response.statusText || '';
    } else {
      console.log("PlayerService -> no Response");
      errMsg = response.message ? response.message : response.toString();
      error.message = errMsg;
    }
    console.log("PlayerService -> throw it to the errorService", error);
    console.log(this.errorsService);
    this.errorsService.addError(error);
    return Observable.throw(errMsg);
  }
}