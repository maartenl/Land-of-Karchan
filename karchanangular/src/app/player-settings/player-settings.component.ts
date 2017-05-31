import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Player } from './player.model';
import { Family } from './family.model';
import { PlayerService} from 'app/player.service';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  /**
   * the model
   */
  player: Player;

  playerForm: FormGroup;
  
  constructor(private playerService: PlayerService,
              private formBuilder: FormBuilder) {
    this.player = new Player(); // dummy player
    this.createForm();
  }

  ngOnInit() {
    this.playerService.getPlayer()
      .subscribe(
        (result: any) => { // on success
          console.log("success!", result);
          this.player = result;
          this.resetForm(result);
        },
        (err: any) => { // error
          // console.log("error", err);
        },
        () => { // on completion
          console.log("ready!");
        }
      );
  }

  createForm() {
    this.playerForm = this.formBuilder.group({
      title: '',
      homepageurl: '',
      imageurl: '',
      dateofbirth: '',
      cityofbirth: '',
      storyline: ''
    });
  }

  resetForm(player: Player) {
    this.playerForm.reset({
      title: player.title,
      homepageurl: player.homepageurl,
      imageurl: player.imageurl,
      dateofbirth: player.dateofbirth,
      cityofbirth: player.cityofbirth,
      storyline: player.storyline
    });
  }
  
  save() {
  }
  
  cancel() {
    this.resetForm(this.player);
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
