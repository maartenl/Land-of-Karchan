import {AfterViewInit, Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {PlayerService} from '../player.service';
import {Guild} from './guild.model';
import {GuildMaster} from './guild-master/guild-master';
import {GuildMembers} from './guild-members/guild-members';
import {Logger} from '../consolelog.service';

@Component({
  selector: 'app-guilds',
  imports: [GuildMaster, GuildMembers],
  templateUrl: './guilds.html',
  styleUrl: './guilds.css',
})
export class Guilds implements AfterViewInit {
  private playerService = inject(PlayerService);

  guildMaster = viewChild.required(GuildMaster);
  guildMember = viewChild.required(GuildMembers);

  title = signal("Unknown guild");

  hasGuild = signal(false);

  ngAfterViewInit() {
    this.playerService.getGuild().subscribe({
        next: (result: Guild) => { // on success
          result.creation = result.creation.replace('[UTC]', '');
          this.title.set(result.title);
          if (this.isGuildMaster(result)) {
            this.guildMaster().setGuild(result);
          } else {
            this.guildMember().setGuild(result);
          }
          this.hasGuild.set(true);
        },
        error: (err: any) => { // error
          // console.log("error", err);
        },
        complete: () => { // on completion
        }
      }
    );
  }

  private isGuildMaster(guild: Guild) {
    return this.playerService.getName() === guild.bossname;
  }

}
