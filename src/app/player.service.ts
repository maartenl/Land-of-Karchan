import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs';

import { environment } from 'app/../environments/environment';

import { Player } from './player-settings/player.model';

@Injectable()
export class PlayerService {
  url: string;

  constructor(private http: Http) { 
    this.url = environment.CHARACTERSHEET_URL;
  }

  public getPlayer() : Observable<any> {
    return this.http.get(this.url);
//      .map((response: Response) => {
//        console.log(response);
//        return response;
//        return (<any>response.json()).items;
//      });
  }
}
