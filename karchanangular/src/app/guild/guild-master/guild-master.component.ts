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
  rankForm: FormGroup;

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
    this.rankForm = this.formBuilder.group({
      guildlevel: null,
      title: ''
    });
  }

  public checkMembers() {
    this.checkRanks();
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
    const styles = {
      // CSS property names
      'color': formModel.colour as string
    };
    return styles;
  }

  save() {
    const newGuild: Guild = this.prepareSaveGuild();
    // TODO: add in the subscribe that the guild is updated.
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
      rank: member.guildrank == null ? -1 : member.guildrank.guildlevel
    });
  }

  resetRankForm(rank: GuildRank) {
    this.rankForm.reset({
      guildlevel: rank.guildlevel,
      title: rank.title
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
    const currentMember: GuildMember = this.guildMembers.currentMember;
    const newMember: GuildMember = this.prepareSaveMember();
    this.playerService.updateGuildmember(newMember).subscribe(
      (result: any) => { currentMember.guildrank = newMember.guildrank; }
    );
  }

  private prepareSaveMember(): GuildMember {
    const formModel = this.memberForm.value;

    const guildlevel: number = formModel.rank as number;
    let guildrank: GuildRank = null;
    if (guildlevel !== -1) {
      guildrank = this.guildRanks.findByGuildlevel(guildlevel);
    }

    // return new `GuildMember` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveMember: GuildMember = {
      name: this.guildMembers.currentMember.name,
      guildrank: guildrank
    };
    return saveMember;
  }

  public selectRank(rank: GuildRank): void {
    this.guildRanks.currentRank = rank;
    this.resetRankForm(rank);
  }

  public saveRank(): void {
    const formModel = this.rankForm.value;

    const guildlevel: number = formModel.guildlevel as number;

    const oldRank: GuildRank = this.guildRanks.findByGuildlevel(guildlevel);
    const newRank: GuildRank = this.prepareSaveRank(oldRank);
    if (oldRank === undefined) {
      // new rank!
      this.playerService.createGuildrank(newRank).subscribe(
        (result: any) => { this.guildRanks.ranks.push(newRank); }
      );
    }
    else {
      // old rank changed!
      this.playerService.updateGuildrank(newRank).subscribe(
        (result: any) => { oldRank.title = newRank.title; }
      );
    }
  }

  private prepareSaveRank(oldRank: GuildRank): GuildRank {
    const formModel = this.rankForm.value;

    // return new `GuildRank` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveRank: GuildRank = {
      guildlevel: formModel.guildlevel as number,
      title: formModel.title as string,
      accept_access: oldRank !== undefined ? oldRank.accept_access : false,
      logonmessage_access: oldRank !== undefined ? oldRank.logonmessage_access : false,
      reject_access: oldRank !== undefined ? oldRank.reject_access : false,
      settings_access: oldRank !== undefined ? oldRank.settings_access : false,
    };
    return saveRank;
  }

}
