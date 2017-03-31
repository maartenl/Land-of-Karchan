import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';

import { Player } from './player.model';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  // static readonly url: string = 'http://www.karchan.org/karchangame/resources/public/charactersheets/Karn';
  static readonly url: string = '/assets/charactersheet.json';

  player: Player;
  data: Object;

  constructor(private http: Http) {
    this.player = new Player();
    http.request(PlayerSettingsComponent.url)
      .subscribe((res: Response) => {
        this.data = res.json();
        this.player = res.json();
      });
  }

  ngOnInit() {
  }

}
