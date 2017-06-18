import { Component, OnInit, Input } from '@angular/core';

import { Guild, GuildHopefuls, GuildRanks, GuildMembers } from '../guild.model';
import { PlayerService } from 'app/player.service';

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
      this.playerService.getGuildmembers().subscribe((result: GuildMembers) => { this.guildMembers = result; });
    }
  }

  public checkRanks() {
    if (this.guildRanks === undefined) {
      this.playerService.getGuildranks().subscribe((result: GuildRanks) => { this.guildRanks = result; });
    }
  }

  public checkHopefuls() {
    if (this.guildHopefuls === undefined) {
      this.playerService.getGuildhopefuls().subscribe((result: GuildHopefuls) => { this.guildHopefuls = result; });
    }
  }

}
