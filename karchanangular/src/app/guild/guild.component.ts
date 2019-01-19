import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Guild, GuildHopefuls, GuildRanks, GuildMembers } from './guild.model';
import { PlayerService } from '../player.service';

@Component({
  selector: 'app-guild',
  templateUrl: './guild.component.html',
  styleUrls: ['./guild.component.css']
})
export class GuildComponent implements OnInit {
  guild: Guild;

  hasGuild = false;

  constructor(private playerService: PlayerService,
    private formBuilder: FormBuilder) {
    this.guild = new Guild();
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getGuild().subscribe(
      (result: Guild) => { // on success
        result.creation = result.creation.replace('[UTC]', '');
        this.guild = result;
        this.hasGuild = true;
      },
      (err: any) => { // error
        // console.log("error", err);
      },
      () => { // on completion
      }
    );
  }

  public isGuildMaster(): boolean {
    if (!this.hasGuild) {
      return false;
    }
    return this.playerService.getName() === this.guild.bossname;
  }

}
