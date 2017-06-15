import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Guild } from './guild.model';
import { PlayerService } from 'app/player.service';

@Component({
  selector: 'app-guild',
  templateUrl: './guild.component.html',
  styleUrls: ['./guild.component.css']
})
export class GuildComponent implements OnInit {

  guild: Guild;

  constructor(private playerService: PlayerService,
    private formBuilder: FormBuilder) {
    this.guild = new Guild()
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getGuild().subscribe(
      (result: Guild) => { // on success
        this.guild = result;
      },
      (err: any) => { // error
        // console.log("error", err);
      },
      () => { // on completion
      }
    );
  }

}
