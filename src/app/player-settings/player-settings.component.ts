import { Component, OnInit } from '@angular/core';

import { Player } from './player.model';
import { PlayerService} from 'app/player.service';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  player: Player;

  constructor(private playerService: PlayerService) {
    this.player = new Player(); // dummy player
  }

  ngOnInit() {
    this.playerService.getPlayer()
      .subscribe(
        (result: any) => { // on success
          console.log("success!", result);
          this.player = result.json();
        },
        (err: any) => { // error
          console.log("error", err);
        },
        () => { // on completion
          console.log("ready!");
        }
      );
  }

}
