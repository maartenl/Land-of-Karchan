import {Component, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';

import {Guild} from './guild.model';
import {PlayerService} from '../player.service';

@Component({
  selector: 'app-guild',
  templateUrl: './guild.component.html',
  styleUrls: ['./guild.component.css']
})
export class GuildComponent implements OnInit {
  guild: Guild | null = null;

  hasGuild = false;

  constructor(private playerService: PlayerService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getGuild().subscribe({
        next: (result: Guild) => { // on success
          result.creation = result.creation.replace('[UTC]', '');
          this.guild = result;
          this.hasGuild = true;
        },
        error: (err: any) => { // error
          // console.log("error", err);
        },
        complete: () => { // on completion
        }
      }
    );
  }

  public isGuildMaster(): boolean {
    if (!this.hasGuild) {
      return false;
    }
    if (this.guild === null) {
      return false;
    }
    return this.playerService.getName() === this.guild.bossname;
  }

}
