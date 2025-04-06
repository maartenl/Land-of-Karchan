import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Guild, GuildHopeful, GuildHopefuls, GuildMember, GuildMembers, GuildRank, GuildRanks} from '../guild.model';
import {PlayerService} from '../../player.service';
import {ToastService} from 'src/app/toast.service';

@Component({
  selector: 'app-guild-master',
  templateUrl: './guild-master.component.html',
  styleUrls: ['./guild-master.component.css']
})
export class GuildMasterComponent implements OnInit {

  @Input() guild: Guild | null = null;
  guildMembers: GuildMembers = new GuildMembers(new Array<GuildMember>(0));
  guildRanks: GuildRanks = new GuildRanks(new Array<GuildRank>(0));
  guildHopefuls: GuildHopefuls = new GuildHopefuls(new Array<GuildHopeful>(0));

  guildForm: FormGroup;
  memberForm: FormGroup;
  rankForm: FormGroup;

  constructor(
    private playerService: PlayerService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    this.guildForm = this.formBuilder.group({
      title: "",
      guildurl: "",
      logonmessage: "",
      guilddescription: "",
      colour: ""
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

  ngOnInit() {
    this.createForms();
  }

  createForms() {
    if (this.guild !== null) {
      this.guildForm = this.formBuilder.group({
        title: this.guild.title,
        guildurl: this.guild.guildurl,
        logonmessage: this.guild.logonmessage,
        guilddescription: this.guild.guilddescription,
        colour: this.guild.colour
      });
    }
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
    const those = this;
    this.playerService.getGuildmembers().subscribe((result: GuildMember[]) => {
      those.guildMembers = new GuildMembers(result);
    });
    return false;
  }

  public checkRanks() {
    const those = this;
    this.playerService.getGuildranks().subscribe((result: GuildRank[]) => {
      those.guildRanks = new GuildRanks(result);
    });
    return false;
  }

  public checkHopefuls() {
    const those = this;
    this.playerService.getGuildhopefuls().subscribe((result: GuildHopeful[]) => {
      those.guildHopefuls = new GuildHopefuls(result);
    });
    return false;
  }

  public getCssColourClass() {
    const formModel = this.guildForm.value;
    const colour = formModel.colour as string
    return "chat-" + colour;
  }

  save() {
    const newGuild: Guild | null = this.prepareSaveGuild();
    // TODO: add in the subscribe that the guild is updated.
    if (newGuild !== null) {
      this.playerService.updateGuild(newGuild).subscribe();
    }
  }

  prepareSaveGuild(): Guild | null {
    const formModel = this.guildForm.value;

    if (this.guild === null) {
      return null;
    }

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
    if (this.guild !== null) {
      this.resetForm(this.guild);
    }
  }

  deleteRank(rank: GuildRank) {
    this.playerService.deleteRank(rank).subscribe((result: any) => {
      this.guildRanks.delete(rank);
      this.toastService.show('Rank successfully deleted.', {
        delay: 3000,
        autohide: true,
        headertext: 'Deleted...'
      });
    });
  }

  deleteMember(member: GuildMember) {
    this.playerService.deleteMember(member).subscribe((result: any) => {
      this.guildMembers.delete(member);
      this.toastService.show('Member successfully deleted.', {
        delay: 3000,
        autohide: true,
        headertext: 'Deleted...'
      });
    });
  }

  deleteHopeful(hopeful: GuildHopeful) {
    this.playerService.deleteHopeful(hopeful).subscribe((result: any) => {
      this.guildHopefuls.delete(hopeful);
      this.toastService.show('Hopeful successfully deleted.', {
        delay: 3000,
        autohide: true,
        headertext: 'Deleted...'
      });
    });
  }

  public selectMember(member: GuildMember): void {
    this.guildMembers.currentMember = member;
    this.resetMemberForm(member);
  }

  public saveMember(): void {
    if (this.guildMembers.currentMember === null) {
      return;
    }
    const currentMember: GuildMember = this.guildMembers.currentMember;
    const newMember: GuildMember | null = this.prepareSaveMember();
    if (newMember === null) {
      return;
    }
    this.playerService.updateGuildmember(newMember).subscribe(
      (result: any) => {
        currentMember.guildrank = newMember.guildrank;
        this.toastService.show('Member updated.', {
          delay: 3000,
          autohide: true,
          headertext: 'Updated...'
        });
      }
    );
  }

  private prepareSaveMember(): GuildMember | null {
    if (this.guildMembers.currentMember === null) {
      return null;
    }
    const formModel = this.memberForm.value;
    const guildlevel: number = formModel.rank as number;
    let guildrank: GuildRank | null = null;
    if (guildlevel !== -1) {
      const stuff = this.guildRanks.findByGuildlevel(guildlevel);
      guildrank = stuff === undefined ? null : stuff;
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

    const oldRank: GuildRank | undefined = this.guildRanks.findByGuildlevel(guildlevel);
    const newRank: GuildRank = this.prepareSaveRank(oldRank);
    if (oldRank === undefined) {
      // new rank!
      this.playerService.createGuildrank(newRank).subscribe(
        (result: any) => {
          this.guildRanks.ranks.push(newRank);
          this.toastService.show('Rank successfully added.', {
            delay: 3000,
            autohide: true,
            headertext: 'Added...'
          });
        }
      );
    } else {
      // old rank changed!
      this.playerService.updateGuildrank(newRank).subscribe(
        (result: any) => {
          oldRank.title = newRank.title;
          this.toastService.show('Rank successfully updated.', {
            delay: 3000,
            autohide: true,
            headertext: 'Updated...'
          });
        }
      );
    }
  }

  private prepareSaveRank(oldRank: GuildRank | undefined): GuildRank {
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
