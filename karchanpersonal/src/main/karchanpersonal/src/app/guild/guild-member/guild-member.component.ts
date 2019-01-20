import { Component, OnInit, Input } from '@angular/core';

import { Guild, GuildHopefuls, GuildRanks, GuildMembers, GuildMember, GuildRank, GuildHopeful } from '../guild.model';
import { PlayerService } from '../../player.service';

@Component({
  selector: 'app-guild-member',
  templateUrl: './guild-member.component.html',
  styleUrls: ['./guild-member.component.css']
})
export class GuildMemberComponent implements OnInit {
  @Input() guild: Guild;
  guildMembers: GuildMembers;
  guildRanks: GuildRanks;
  guildHopefuls: GuildHopefuls;

  constructor(private playerService: PlayerService) { }

  ngOnInit() {
  }

  public checkMembers() {
    if (this.guildMembers === undefined) {
      this.playerService.getGuildmembers().subscribe((result: GuildMember[]) => {
        this.guildMembers = new GuildMembers(result);
      });
    }
  }

  public checkRanks() {
    if (this.guildRanks === undefined) {
      this.playerService.getGuildranks().subscribe((result: GuildRank[]) => {
        this.guildRanks = new GuildRanks(result);
      });
    }
  }

  public checkHopefuls() {
    if (this.guildHopefuls === undefined) {
      this.playerService.getGuildhopefuls().subscribe((result: GuildHopeful[]) => {
        this.guildHopefuls = new GuildHopefuls(result);
      });
    }
  }

}
