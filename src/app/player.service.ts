import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

import { environment } from 'app/../environments/environment';

import { Player } from './player-settings/player.model';

@Injectable()
export class PlayerService {
  url: string;

  constructor(private http: Http) { 
    this.url = environment.CHARACTERSHEET_URL;
  }

  public getPlayer() : Observable<any> {
    return this.http.get(this.url)
                    .map(this.extractData)
//      .map((response: Response) => {
//        console.log(response);
//        return response;
//        return (<any>response.json()).items;
//      });
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || { };
  }

  private handleError (error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} $}err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
  }
}