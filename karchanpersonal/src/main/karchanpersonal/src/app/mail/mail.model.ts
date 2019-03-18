
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
  selected = false;

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

}

export class MailList {
  mails: Mail[];
  page: number;
  allSelected = false;

  constructor() {
    this.mails = void 0;
    this.page = 0;
  }

  public setMails(mails: Mail[]) {
    this.mails = mails;
  }

  /**
   * Retrieves the current page, i.e. the 20 mails from the offset.
   */
  public getMails(): Mail[] {
    if (this.mails === undefined) {
      return [];
    }
    return this.mails.slice(this.page * 20, this.page * 20 + 20);
  }

  public getNumberOfMails(): number {
    if (this.mails === undefined) {
      return 0;
    }
    return this.mails.length;
  }

  public delete(mail: Mail) {
    const index: number = this.mails.indexOf(mail);
    if (index !== -1) {
      this.mails.splice(index, 1);
    }
  }

  /**
   * Basically, divides the number of mails by 20. So:
   * <ul><li>if you have no mails, you have 1 page.</li>
   * <li>if you have 19 mails, you have 1 page.</li>
   * <li>if you have 20 mails, you have 1 page.</li>
   * <li>if you have 21 mails, you have 2 pages.</li>
   * <li>if you have 40 mails, you have 2 pages.</li>
   * <li>if you have 41 mails, you have 3 pages.</li></ul>
   */
  public getNumberOfPages(): number {
    // 0 / 20 + 1 => 1
    // 19 / 20 + 1 => 1
    // 20 / 20 + 1 => 1
    // 21 / 20 + 1 => 2
    // 40 / 20 + 1 => 2
    // 41 / 20 + 1 => 3
    if (this.getNumberOfMails() === 0) {
      return 1;
    }
    return Math.floor((this.getNumberOfMails() - 1) / 20) + 1;
  }

  public generatePages(): number[] {
    const numbers: number[] = [];
    for (let i = 1; i <= this.getNumberOfPages(); i++) {
      numbers.push(i);
    }
    return numbers;
  }

  public previous(): void {
    if (this.page > 0) {
      this.page--;
    }
  }

  public next(): void {
    if (this.page < this.getNumberOfPages()) {
      this.page++;
    }
  }

  public setPage(page: number): void {
    this.page = page;
  }

  public addAll(newMails: Mail[]) {
    if (this.mails === undefined) {
      this.mails = [];
    }
    this.mails = this.mails.concat(newMails);
  }

  public toggleAll(): void {
    for (const mail of this.getMails()) {
      mail.selected = !this.allSelected;
    }
    this.allSelected = !this.allSelected;
  }

  public isAllSelected(): boolean {
    return this.allSelected;
  }

  public getSelectedMails(): Mail[] {
    return this.mails.filter((mail) => mail.selected);
  }
}
