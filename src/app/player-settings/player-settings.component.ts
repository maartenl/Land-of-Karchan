import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';
import { environment } from 'app/../environments/environment';

import { Player } from './player.model';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  url: string;
  player: Player;
  data: Object;

  constructor(private http: Http) {
    this.player = new Player();
    this.url = environment.CHARACTERSHEET_URL;
    http.request(this.url)
      .subscribe((res: Response) => {
        this.data = res.json();
        this.player = res.json();
      });
  }

  ngOnInit() {
  }

}
