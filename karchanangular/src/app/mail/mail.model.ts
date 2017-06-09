import { JsonProperty } from 'json-typescript-mapper';

export class Mail {
  name: string;
  toname: string;
  subject: string;
  body: string;
  id: string;
  haveread: boolean;
  newmail: boolean;
  whensent: string;
  deleted: boolean;

 public static readonly MONTHS = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
  ];


  constructor() {
    this.name = void 0;
    this.toname = void 0; 
    this.subject = void 0; 
    this.body = void 0; 
    this.id = void 0; 
    this.haveread = void 0; 
    this.newmail = void 0; 
    this.whensent = void 0; 
    this.deleted = void 0; 
  }

  public getWhen(): string {
    let date: Date = new Date(this.whensent);
    let now: Date = new Date();
    if (now.getFullYear() === date.getFullYear()) {
      return Mail.MONTHS[date.getMonth()] + " " + date.getDate();
    }
    return Mail.MONTHS[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear();
  }

}

export class MailList {
  @JsonProperty({ clazz: Mail, name: 'mails' })
  mails: Mail[];

  constructor() {
    this.mails = void 0;
  }
}

