import { Component, OnInit, Input } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Guild, GuildHopefuls, GuildHopeful, GuildRanks, GuildRank, GuildMembers, GuildMember } from '../guild.model';
import { PlayerService } from 'app/player.service';

@Component({
  selector: 'app-guild-master',
  templateUrl: './guild-master.component.html',
  styleUrls: ['./guild-master.component.css']
})
export class GuildMasterComponent implements OnInit {

  @Input() guild: Guild;
  guildMembers: GuildMembers;
  guildRanks: GuildRanks;
  guildHopefuls: GuildHopefuls;

  guildForm: FormGroup;
  memberForm: FormGroup;

  constructor(private playerService: PlayerService,
    private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.createForms();
  }

  createForms() {
    this.guildForm = this.formBuilder.group({
      title: this.guild.title,
      guildurl: this.guild.guildurl,
      logonmessage: this.guild.logonmessage,
      guilddescription: this.guild.guilddescription,
      colour: this.guild.colour
    });
    this.memberForm = this.formBuilder.group({
      name: '',
      rank: null
    });
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

  public getStyle() {
    const formModel = this.guildForm.value;
    let styles = {
      // CSS property names
      'color': formModel.colour as string
    };
    return styles;
  }

  save() {
    let newGuild: Guild = this.prepareSaveGuild();
    this.playerService.updateGuild(newGuild).subscribe();
  }

  prepareSaveGuild(): Guild {
    const formModel = this.guildForm.value;

    // return new `Guild` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveGuild: Guild = {
      name: this.guild.name,
      logonmessage: formModel.logonmessage as string,
      colour: formModel.colour as string,
      guildurl: formModel.guildurl as string,
      title: formModel.title as string,
      bossname: this.guild.bossname as string,
      guilddescription: formModel.guilddescription as string,
      creation: this.guild.creation as string
    };
    return saveGuild;
  }

  resetForm(guild: Guild) {
    this.guildForm.reset({
      title: guild.title,
      guildurl: guild.guildurl,
      logonmessage: guild.logonmessage,
      guilddescription: guild.guilddescription,
      colour: guild.colour
    });
  }

  resetMemberForm(member: GuildMember) {
    this.memberForm.reset({
      name: member.name,
      rank: member.guildrank
    });
  }

  cancel() {
    this.resetForm(this.guild);
  }

  deleteRank(rank: GuildRank) {
    this.playerService.deleteRank(rank).subscribe((result: any) => { this.guildRanks.delete(rank); });
  }

  deleteMember(member: GuildMember) {
    this.playerService.deleteMember(member).subscribe((result: any) => { this.guildMembers.delete(member); });
  }

  deleteHopeful(hopeful: GuildHopeful) {
    this.playerService.deleteHopeful(hopeful).subscribe((result: any) => { this.guildHopefuls.delete(hopeful); });
  }

  public selectMember(member: GuildMember): void {
    this.guildMembers.currentMember = member;
    this.resetMemberForm(member);
  }

  public saveMember(): void {
    console.log("saveMember ", this.guildMembers.currentMember);
  }

}
