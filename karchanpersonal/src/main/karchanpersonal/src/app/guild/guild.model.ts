
export class GuildRank {
  title: string;
  guildlevel: number;
  accept_access: boolean;
  reject_access: boolean;
  settings_access: boolean;
  logonmessage_access: boolean;

  constructor() {
    this.title = void 0;
    this.guildlevel = void 0;
    this.accept_access = void 0;
    this.reject_access = void 0;
    this.settings_access = void 0;
    this.logonmessage_access = void 0;
  }
}

export interface GuildMember {
  name: string;
  guildrank: GuildRank;
}

export interface GuildHopeful {
  name: string;
  guild: string;
  guildrank: string;
}

export class GuildHopefuls {
  hopefuls: GuildHopeful[];

  constructor(guildHopefuls: GuildHopeful[]) {
    this.hopefuls = guildHopefuls;
  }

  public delete(hopeful: GuildHopeful) {
    const index: number = this.hopefuls.indexOf(hopeful);
    if (index !== -1) {
      this.hopefuls.splice(index, 1);
    }
  }
}

export class GuildMembers {
  members: GuildMember[];

  currentMember: GuildMember;

  constructor(guildMembers: GuildMember[]) {
    this.members = guildMembers;
  }

  public delete(member: GuildMember) {
    const index: number = this.members.indexOf(member);
    if (index !== -1) {
      this.members.splice(index, 1);
    }
  }
}

export class GuildRanks {
  ranks: GuildRank[];

  currentRank: GuildRank;

  constructor(guildRanks: GuildRank[]) {
    this.ranks = guildRanks;
  }

  public delete(rank: GuildRank) {
    const index: number = this.ranks.indexOf(rank);
    if (index !== -1) {
      this.ranks.splice(index, 1);
    }
  }

  public findByGuildlevel(guildlevel: number): GuildRank {
    // cannot use triple equals here, guildlevel might be a string.
    return this.ranks.find((rank) => rank.guildlevel == guildlevel);
  }

}

export interface Guild {
  name: string;
  logonmessage: string;
  colour: string;
  guildurl: string;
  title: string;
  bossname: string;
  guilddescription: string;
  creation: string;
}
