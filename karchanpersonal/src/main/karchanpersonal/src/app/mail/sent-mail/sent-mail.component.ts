import { Component, OnInit } from '@angular/core';

import { Mail, MailList } from '../mail.model';
import { PlayerService } from '../../player.service';
import { ToastService } from '../../toast.service';

@Component({
  selector: 'app-sent-mail',
  templateUrl: './sent-mail.component.html',
  styleUrls: ['./sent-mail.component.css']
})
export class SentMailComponent implements OnInit {
  mails: MailList;

  mail: Mail;

  constructor(
    private playerService: PlayerService,
    private toastService: ToastService) {
    this.mails = new MailList();
    this.mail = new Mail();
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getSentMail(this.mails.getNumberOfMails()).subscribe(
      (result: Mail[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach(value =>
            value.whensent = value.whensent.replace('[UTC]', '')
          );
          this.mails.addAll(result);
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public setMail(mail: Mail): void {
    this.mail = mail;
  }

  public previous(): boolean {
    this.mails.previous();
    return false;
  }

  public setPage(page: number): boolean {
    this.mails.setPage(page);
    return false;
  }

  public next(): boolean {
    if (this.mails.page !== this.mails.getNumberOfPages() - 1) {
      this.mails.next();
      return;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getSentMail(this.mails.getNumberOfMails()).subscribe(
      (result: Mail[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach(value =>
            value.whensent = value.whensent.replace('[UTC]', '')
          );
          this.mails.addAll(result);
          this.mails.next();
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
    return false;
  }


}
