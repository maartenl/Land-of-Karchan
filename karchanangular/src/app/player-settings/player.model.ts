import { Family } from "./family.model";

export class Player {
  name: string;
  title: string;
  sex: string;
  description: string;
  imageurl: string;
  guild: string;
  homepageurl : string;
  dateofbirth: string;
  cityofbirth: string;
  storyline: string;
  
  familyvalues: Family[];

  constructor() {
  }

}
