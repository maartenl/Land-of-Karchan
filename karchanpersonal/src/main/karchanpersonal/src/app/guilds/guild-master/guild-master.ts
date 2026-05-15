import {Component, inject, input, signal} from '@angular/core';
import {Guild, GuildHopeful, GuildMember, GuildRank} from '../guild.model';
import {PlayerService} from '../../player.service';
import {ToastService} from '../../toast.service';
import {form, FormField} from '@angular/forms/signals';
import {DatePipe, NgClass} from '@angular/common';
import {Logger} from '../../consolelog.service';
import {NgbCollapse} from '@ng-bootstrap/ng-bootstrap';

export interface MemberModel {
  name: string;
  rank: string;
}

export interface RankModel {
  guildlevel: number;
  title: string;
}

@Component({
  selector: 'app-guild-master',
  imports: [DatePipe, FormField, NgClass, NgbCollapse],
  templateUrl: './guild-master.html',
  styleUrl: './guild-master.css',
})
export class GuildMaster {
  private playerService = inject(PlayerService)
  private toastService = inject(ToastService)

  hasGuild = signal(false);

  guildMembers = signal<GuildMember[]>(new Array<GuildMember>(0))
  guildRanks = signal<GuildRank[]>(new Array<GuildRank>(0))
  guildHopefuls = signal<GuildHopeful[]>(new Array<GuildHopeful>(0))

  isCollapsedMembers = true
  isCollapsedRanks = true
  isCollapsedHopefuls = true

  guildModel = signal<Guild>({
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
  memberModel = signal<MemberModel>({name: '', rank: "-1"})
  rankModel = signal<RankModel>({guildlevel: -1, title: ''})

  guildForm = form(this.guildModel);
  memberForm = form(this.memberModel);
  rankForm = form(this.rankModel);

  public setGuild(guild: Guild) {
    Logger.logEntering("GuildMaster.setGuild: " + guild.name);
    this.guildModel.set(guild);
    this.hasGuild.set(true);
    this.guildRanks.set(guild.guildRanks);
    this.guildHopefuls.set(guild.guildHopefuls);
    this.guildMembers.set(guild.guildMembers);
  }

  public getCssColourClass() {
    const formModel = this.guildModel();
    const colour = formModel.colour as string
    return "chat-" + colour;
  }

  save(event: Event) {
    event.preventDefault();
    const newGuild: Guild = this.guildModel();
    // TODO: add in the subscribe that the guild is updated.
    if (newGuild !== null) {
      this.playerService.updateGuild(newGuild).subscribe();
    }
  }

  deleteRank(rank: GuildRank) {
    this.playerService.deleteRank(rank).subscribe((result: any) => {
      const index: number = this.guildRanks().indexOf(rank);
      if (index !== -1) {
        this.guildRanks.update(ranks => ranks.splice(index, 1));
      }
      this.toastService.showMessage("Rank successfully deleted.", "Deleted...");
    });
  }

  deleteMember(member: GuildMember) {
    this.playerService.deleteMember(member).subscribe((result: any) => {
      const index: number = this.guildMembers().indexOf(member);
      if (index !== -1) {
        this.guildMembers.update(ranks => ranks.splice(index, 1));
      }
      this.toastService.showMessage("Member successfully deleted.", "Deleted...");
    });
  }

  deleteHopeful(hopeful: GuildHopeful) {
    this.playerService.deleteHopeful(hopeful).subscribe((result: any) => {
      const index: number = this.guildHopefuls().indexOf(hopeful);
      if (index !== -1) {
        this.guildHopefuls.update(ranks => ranks.splice(index, 1));
      }
      this.toastService.showMessage("Hopeful successfully deleted.", "Deleted...");
    });
  }

  public selectMember(member: GuildMember): void {
    const rank = member.guildrank?.guildlevel;
    const defRank = rank ?? -1;
    this.memberModel.set({name: member.name, rank: String(defRank)});
  }

  public saveMember(event: Event): void {
    event.preventDefault();
    const newMember: MemberModel = this.memberModel()
    const guildrank = this.guildRanks().filter(rank => rank.guildlevel === Number(newMember.rank)).at(0);
    if (guildrank === undefined) {
      this.toastService.showMessage("Rank not found.", "'Error...");
      return;
    }
    let guildMember = this.guildMembers().filter(member => member.name === newMember.name).at(0);
    let found = true;
    if (guildMember === undefined) {
      found = false;
      guildMember = {name: newMember.name, guildrank: guildrank}
    } else {
      guildMember.guildrank = guildrank;
    }
    this.playerService.updateGuildmember(guildMember).subscribe(
      (result: any) => {
        if (found) {
          // do nothing, see if that works. :)
        } else {
          this.guildMembers.update(members => {
              members.push(guildMember);
              return members;
            }
          )
        }
        this.toastService.showMessage("Member updated.", "Updated...");
      }
    );
  }

  public selectRank(rank: GuildRank): void {
    this.rankModel.set({guildlevel: rank.guildlevel, title: rank.title});
  }

  public saveRank(event: Event): void {
    event.preventDefault();
    const formModel = this.rankModel();

    const guildlevel: number = formModel.guildlevel;

    const oldRank: GuildRank | undefined = this.guildRanks().filter(rank => rank.guildlevel === guildlevel).at(0);
    const newRank: GuildRank = oldRank ??
      {
        title: formModel.title,
        guildlevel: formModel.guildlevel,
        // TODO MLE: Fix this!
        accept_access: false,
        logonmessage_access: false,
        reject_access: false,
        settings_access: false,
      };
    if (oldRank === undefined) {
      // new rank!
      this.playerService.createGuildrank(newRank).subscribe(
        (result: any) => {
          this.guildRanks.update(ranks => {
            ranks.push(newRank);
            return ranks;
          })
          this.toastService.showMessage("Rank successfully added.", "Added...");
        }
      );
    } else {
      // old rank changed!
      this.playerService.updateGuildrank(newRank).subscribe(
        (result: any) => {
          oldRank.title = newRank.title;
          this.toastService.showMessage("Rank successfully updated.", "Updated...");
        }
      );
    }
  }
}
