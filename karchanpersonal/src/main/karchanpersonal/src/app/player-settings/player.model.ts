import { Family } from "./family.model";

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

  familyvalues: Family[];

  constructor() {
    this.name = '';
    this.title = ''; 
    this.sex = ''; 
    this.description = ''; 
    this.imageurl = ''; 
    this.guild = ''; 
    this.homepageurl = ''; 
    this.dateofbirth = ''; 
    this.cityofbirth = ''; 
    this.storyline = '';
    this.familyvalues = new Array<Family>(0);
  }

}
