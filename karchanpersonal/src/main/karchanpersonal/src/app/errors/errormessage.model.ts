
export class ErrorMessage {
  type: string;
  message: string;
  detailedmessage: string;
  showDetail: boolean = false;

  constructor() {
  }

  public hasDetailedMessage(): boolean {
    return this.detailedmessage != null;
  }

  public showDetailedMessage(): boolean {
    return this.hasDetailedMessage() && this.showDetail;
  }

  public toggleDetails() {
    this.showDetail = !this.showDetail;
  }
}
