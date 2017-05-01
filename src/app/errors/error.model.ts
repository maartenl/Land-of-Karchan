
export class Error {
  type: string;
  message: string;
  detailedmessage: string;

  constructor() {
  }

  public hasDetailedMessage(): boolean {
    return this.detailedmessage != null;
  }

}
