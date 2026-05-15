import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {PlayerService} from '../../player.service';
import {Mail} from '../mail.model';
import {ShowMail} from '../show-mail/show-mail';
import {DatePipe, NgClass} from '@angular/common';
import {Logger} from '../../consolelog.service';
import {MailService} from '../../mail.service';

@Component({
  selector: 'app-sent-mail',
  imports: [ShowMail, DatePipe, NgClass],
  templateUrl: './sent-mail.html',
  styleUrl: './sent-mail.css',
})
export class SentMail implements OnInit {
  private playerService: PlayerService = inject(PlayerService);
  private mailService: MailService = inject(MailService)

  showMail = viewChild(ShowMail);

  mails = signal(new Array<Mail>());

  mail = signal(new Mail());

  ngOnInit() {
    if (this.getMailList().getNumberOfMails() != 0) {
      this.updateMails();
      return;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getSentMail(this.getMailList().getNumberOfMails()).subscribe({
        next: (result: Mail[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
                if (value.whensent !== null) {
                  value.whensent = value.whensent.replace('[UTC]', '')
                }
              }
            );
            this.getMailList().addAll(result);
            this.updateMails()
          }
        }
      }
    );
  }


  public setMail(mail: Mail): void {
    this.mail.set(mail);
    this.showMail()?.setMail(mail);
  }

  public previous(): boolean {
    this.getMailList().previous();
    this.updateMails();
    return false;
  }

  public setPage(page: number): boolean {
    this.getMailList().setPage(page);
    this.updateMails();
    return false;
  }

  public next(): boolean {
    if (this.getMailList().page !== this.getMailList().getNumberOfPages() - 1) {
      this.getMailList().next();
      this.updateMails();
      return true;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getSentMail(this.getMailList().getNumberOfMails()).subscribe({
        next: (result: Mail[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
                if (value.whensent !== null) {
                  value.whensent = value.whensent.replace('[UTC]', '')
                }
              }
            );
            this.getMailList().addAll(result);
            this.updateMails()
          }
        }
      }
    );
    return false;
  }

  private updateMails() {
    Logger.logEntering('updateMails');
    var mails = this.getMailList().getMails()
    this.mails.set(mails);
    Logger.logExiting('updateMails');
  }

  protected getMailList() {
    return this.mailService.sentList;
  }
}
