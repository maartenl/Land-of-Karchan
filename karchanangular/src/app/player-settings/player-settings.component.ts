import { Component, OnInit } from '@angular/core';

import { Player } from './player.model';
import { Family } from './family.model';
import { PlayerService} from 'app/player.service';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  player: Player;
  
  editMode: boolean;

  constructor(private playerService: PlayerService) {
    this.player = new Player(); // dummy player
    this.editMode = false;
  }

  ngOnInit() {
    this.playerService.getPlayer()
      .subscribe(
        (result: any) => { // on success
          console.log("success!", result);
          this.player = result;
        },
        (err: any) => { // error
          // console.log("error", err);
        },
        () => { // on completion
          console.log("ready!");
        }
      );
  }
  
  edit() {
    this.editMode = true;
  }
  
  save() {
    this.editMode = false;
  }
  
  cancel() {
    this.editMode = false;
  }
  
  delete(family: Family) {
    console.log(family);
    let index = this.player.familyvalues.indexOf(family, 0);
    if (index > -1) {
      this.player.familyvalues.splice(index, 1);
    }
    // TODO: go to http service with this.
  }

}
