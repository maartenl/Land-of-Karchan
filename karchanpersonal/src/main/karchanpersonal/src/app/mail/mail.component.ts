import { Component, OnInit } from '@angular/core';

import { Mail, MailList } from './mail.model';
import { PlayerService } from '../player.service';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-mail',
  templateUrl: './mail.component.html',
  styleUrls: ['./mail.component.css']
})
export class MailComponent implements OnInit {
  public static readonly MONTHS = [
    'Jan',
    'Feb',
    'Mar',
    'Apr',
    'May',
    'Jun',
    'Jul',
    'Aug',
    'Sep',
    'Oct',
    'Nov',
    'Dec'
  ];

  mails: MailList;

  mail: Mail;

  /**
   * The mail to be used to reply to.
   */
  originalMail: Mail;

  constructor(
    private playerService: PlayerService,
    private toastService: ToastService) {
    this.mails = new MailList();
    this.mail = new Mail();
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(this.mails.getNumberOfMails()).subscribe(
      (result: Mail[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach(function (value) {
            value.whensent = value.whensent.replace('[UTC]', '');
          });
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

  replyMail(mail: Mail) {
    this.originalMail = mail;
    console.log('replyMail');
    return false;
  }

  public setMail(mail: Mail): void {
    this.mail = mail;
  }

  public deleteMail(mail: Mail): void {
    this.playerService.deleteMail(mail).subscribe(
      (result: any) => { // on success
        this.mails.delete(mail);
        this.toastService.show('Mail deleted.', {
          delay: 3000,
          autohide: true,
          headertext: 'Deleted...'
        });
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }
  public deleteSelectedMails(): void {
    this.mails.getSelectedMails().forEach((mail) => this.deleteMail(mail));
  }

  public next(): void {
    if (this.mails.page !== this.mails.getNumberOfPages() - 1) {
      this.mails.next();
      return;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(this.mails.getNumberOfMails()).subscribe(
      (result: Mail[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach(function (value) {
            value.whensent = value.whensent.replace('[UTC]', '');
          });
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
  }

}
