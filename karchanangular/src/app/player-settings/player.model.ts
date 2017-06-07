import { Family } from "./family.model";
import { JsonProperty } from 'json-typescript-mapper';

export class Player {
  name: string;
  title: string;
  sex: string;
  description: string;
  imageurl: string;
  guild: string;
  homepageurl: string;
  dateofbirth: string;
  cityofbirth: string;
  storyline: string;

  @JsonProperty({ clazz: Family, name: 'familyvalues' })
  familyvalues: Family[];

  constructor() {
    this.name = void 0;
    this.title = void 0; 
    this.sex = void 0; 
    this.description = void 0; 
    this.imageurl = void 0; 
    this.guild = void 0; 
    this.homepageurl = void 0; 
    this.dateofbirth = void 0; 
    this.cityofbirth = void 0; 
    this.storyline = void 0;
    this.familyvalues = void 0;
  }

}
