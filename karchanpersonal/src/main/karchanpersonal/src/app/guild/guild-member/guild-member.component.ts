import { Component, OnInit, Input } from '@angular/core';

import { Guild, GuildHopefuls, GuildRanks, GuildMembers, GuildMember, GuildRank, GuildHopeful } from '../guild.model';
import { PlayerService } from '../../player.service';

@Component({
  selector: 'app-guild-member',
  templateUrl: './guild-member.component.html',
  styleUrls: ['./guild-member.component.css']
})
export class GuildMemberComponent implements OnInit {
  @Input() guild: Guild | null = null;
  guildMembers: GuildMembers = new GuildMembers(new Array<GuildMember>(0));
  guildRanks: GuildRanks = new GuildRanks(new Array<GuildRank>(0));
  guildHopefuls: GuildHopefuls = new GuildHopefuls(new Array<GuildHopeful>(0));

  constructor(private playerService: PlayerService) { }

  ngOnInit() {
  }

  public checkMembers() {
    this.playerService.getGuildmembers().subscribe((result: GuildMember[]) => {
      this.guildMembers = new GuildMembers(result);
    });
    return false;
  }

  public checkRanks() {
    this.playerService.getGuildranks().subscribe((result: GuildRank[]) => {
      this.guildRanks = new GuildRanks(result);
    });
    return false;
  }

  public checkHopefuls() {
    this.playerService.getGuildhopefuls().subscribe((result: GuildHopeful[]) => {
      this.guildHopefuls = new GuildHopefuls(result);
    });
    return false;
  }

}
