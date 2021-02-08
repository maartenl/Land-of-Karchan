
export class ErrorMessage {
  type: string | null = null;
  message: string | null = null;
  detailedmessage: string | null = null;
  showDetail = false;

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
