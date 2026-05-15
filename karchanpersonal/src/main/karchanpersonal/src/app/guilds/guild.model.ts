
export class GuildRank {
  title: string;
  guildlevel: number;
  accept_access: boolean;
  reject_access: boolean;
  settings_access: boolean;
  logonmessage_access: boolean;

  constructor() {
    this.title = "";
    this.guildlevel = 0;
    this.accept_access = false;
    this.reject_access = false;
    this.settings_access = false;
    this.logonmessage_access = false;
  }
}

export interface GuildMember {
  name: string;
  guildrank: GuildRank | null;
}

export interface GuildHopeful {
  name: string;
  guild: string;
  guildrank: string;
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

  guildMembers: GuildMember[];
  guildRanks: GuildRank[];
  guildHopefuls: GuildHopeful[];
}
