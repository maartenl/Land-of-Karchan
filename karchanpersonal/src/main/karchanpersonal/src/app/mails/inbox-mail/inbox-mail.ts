import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {ComposeMail} from '../compose-mail/compose-mail';
import {Mail, MailList} from '../mail.model';
import {PlayerService} from '../../player.service';
import {ToastService} from '../../toast.service';
import {ShowMail} from '../show-mail/show-mail';
import {DatePipe, NgClass} from '@angular/common';
import {Logger} from '../../consolelog.service';
import {MailService} from '../../mail.service';

@Component({
  selector: 'app-inbox-mail',
  imports: [
    ComposeMail, ShowMail, DatePipe, NgClass
  ],
  templateUrl: './inbox-mail.html',
  styleUrl: './inbox-mail.css',
})
export class InboxMail implements OnInit {

  composeMail = viewChild(ComposeMail);
  showMail = viewChild(ShowMail);

  private playerService: PlayerService = inject(PlayerService)
  private toastService: ToastService = inject(ToastService)
  private mailService: MailService = inject(MailService)

  mails = signal(new Array<Mail>());

  mail = signal(new Mail());

  ngOnInit(): void {
    if (this.getMailList().getNumberOfMails() != 0) {
      this.updateMails();
      return;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(0).subscribe({
        next: (result: Mail[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
                if (value.whensent !== null) {
                  value.whensent = value.whensent.replace('[UTC]', '')
                }
              }
            );
            this.getMailList().addAll(result);
            this.updateMails();
          }
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );
  }

  replyMail(mail: Mail) {
    this.composeMail()?.setOriginalMail(mail);
    return false;
  }

  replyAllMail(mail: Mail) {
    const newmail = new Mail(mail);
    newmail.name = newmail.name + ', ' + newmail.toname;
    this.composeMail()?.setOriginalMail(newmail);
    return false;
  }

  public setMail(mail: Mail): void {
    this.mail.set(mail);
    this.showMail()?.setMail(mail);
  }

  public deleteMail(mail: Mail): boolean {
    this.playerService.deleteMail(mail).subscribe({
        next: (result: any) => { // on success
          this.getMailList().delete(mail);
          this.updateMails();
          this.toastService.showMessage("Mail deleted.", "Deleted...");
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );
    return false;
  }

  public deleteSelectedMails(): boolean {
    this.getMailList().getSelectedMails().forEach((mail) => this.deleteMail(mail));
    return false;
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

  public toggleMail(mail: Mail) {
    mail.selected = !mail.selected;
    return false;
  }

  public toggleAll(): boolean {
    this.getMailList().toggleAll();
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
    this.playerService.getMail(this.getMailList().getNumberOfMails()).subscribe({
        next: (result: Mail[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
                if (value.whensent !== null) {
                  value.whensent = value.whensent.replace('[UTC]', '')
                }
              }
            );
            this.getMailList().addAll(result);
            this.updateMails();
          }
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
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
    return this.mailService.inboxList;
  }

}
