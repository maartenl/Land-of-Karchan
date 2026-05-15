
export class ErrorMessage {
  private static nextId = 0;
  id: number;
  type: string = "";
  message: string = "";
  detailedmessage: string = "";
  showDetail = false;

  constructor() {
    this.id = ErrorMessage.nextId++;
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
