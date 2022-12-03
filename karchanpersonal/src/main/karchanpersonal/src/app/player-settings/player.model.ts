import {Family} from "./family.model";

export class Player {
  name: string;
  title: string;
  familyname: string;
  sex: string;
  description: string;
  imageurl: string;
  guild: string;
  homepageurl: string;
  dateofbirth: string;
  cityofbirth: string;
  storyline: string;
  websockets: boolean;

  familyvalues: Family[];

  constructor() {
    this.name = '';
    this.title = '';
    this.familyname = '';
    this.sex = '';
    this.description = '';
    this.imageurl = '';
    this.guild = '';
    this.homepageurl = '';
    this.dateofbirth = '';
    this.cityofbirth = '';
    this.storyline = '';
    this.familyvalues = new Array<Family>(0);
    this.websockets = true;
  }

}

export class PasswordReset {
  name: string | null;
  oldpassword: string | null;
  password: string | null;
  password2: string | null;

  constructor(object?: any) {
    if (object === undefined) {
      this.name = null;
      this.oldpassword = null;
      this.password = null;
      this.password2 = null;
      return;
    }
    this.name = object.name;
    this.oldpassword = object.oldpassword;
    this.password = object.password;
    this.password2 = object.password2;
  }

}
