import { JsonProperty } from 'json-typescript-mapper';

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
