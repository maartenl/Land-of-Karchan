import {Component, inject, input, signal} from '@angular/core';
import {Guild, GuildHopeful, GuildMember, GuildRank} from '../guild.model';
import {Logger} from '../../consolelog.service';
import {PlayerService} from '../../player.service';
import {DatePipe, NgClass} from '@angular/common';
import {FormField} from '@angular/forms/signals';
import {NgbCollapse} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-guild-member',
  imports: [DatePipe, NgbCollapse],
  templateUrl: './guild-members.html',
  styleUrl: './guild-members.css',
})
export class GuildMembers {
  private playerService = inject(PlayerService);

  hasGuild = signal(false);

  guild = signal<Guild>({
    name: '',
    logonmessage: '',
    colour: '',
    guildurl: '',
    title: '',
    bossname: '',
    guilddescription: '',
    creation: '',
    guildMembers: [],
    guildHopefuls: [],
    guildRanks: [],
  })

  guildMembers = signal<GuildMember[]>(new Array<GuildMember>(0))
  guildRanks = signal<GuildRank[]>(new Array<GuildRank>(0))
  guildHopefuls = signal<GuildHopeful[]>(new Array<GuildHopeful>(0))

  isCollapsedMembers = true
  isCollapsedRanks = true
  isCollapsedHopefuls = true

  public setGuild(guild: Guild) {
    Logger.logEntering("GuildMember.setGuild: " + guild.name);
    this.guild.set(guild);
    this.hasGuild.set(true);
    this.guildRanks.set(guild.guildRanks);
    this.guildHopefuls.set(guild.guildHopefuls);
    this.guildMembers.set(guild.guildMembers);
  }


}
