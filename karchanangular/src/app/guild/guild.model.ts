import { JsonProperty } from 'json-typescript-mapper';

export class GuildMember {
  name: string;
  guildrank: string;

  constructor() {
    this.name = void 0;
    this.guildrank = void 0;
  }
}

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

export class GuildHopeful {
  name: string;
  guild: string;
  guildrank: string;

  constructor() {
    this.name = void 0;
    this.guild = void 0;
    this.guildrank = void 0;
  }
}

export class GuildHopefuls {
  @JsonProperty({ clazz: GuildHopeful, name: 'hopefuls' })
  hopefuls: GuildHopeful[];

  constructor() {
    this.hopefuls = void 0;
  }

  public delete(hopeful: GuildHopeful) {
    let index: number = this.hopefuls.indexOf(hopeful);
    if (index !== -1) {
      this.hopefuls.splice(index, 1);
    }
  }
}

export class GuildMembers {
  @JsonProperty({ clazz: GuildMember, name: 'members' })
  members: GuildMember[];

  currentMember: GuildMember;

  constructor() {
    this.members = void 0;
  }

  public delete(member: GuildMember) {
    let index: number = this.members.indexOf(member);
    if (index !== -1) {
      this.members.splice(index, 1);
    }
  }
}

export class GuildRanks {
  @JsonProperty({ clazz: GuildRank, name: 'ranks' })
  ranks: GuildRank[];

  constructor() {
    this.ranks = void 0;
  } 
  
  public delete(rank: GuildRank) {
    let index: number = this.ranks.indexOf(rank);
    if (index !== -1) {
      this.ranks.splice(index, 1);
    }
  }

}

export class Guild {
  name: string;
  logonmessage: string;
  colour: string;
  guildurl: string;
  title: string;
  bossname: string;
  guilddescription: string;
  creation: string;

  constructor() {
    this.name = void 0;
    this.logonmessage = void 0;
    this.colour = void 0;
    this.guildurl = void 0;
    this.title = void 0;
    this.bossname = void 0;
    this.guilddescription = void 0;
    this.creation = void 0;
  }
}
