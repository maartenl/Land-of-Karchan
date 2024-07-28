import {Component, EventEmitter, OnInit, Output} from '@angular/core';

import {Mail, MailList} from '../mail.model';
import {PlayerService} from '../../player.service';
import {ToastService} from '../../toast.service';

@Component({
  selector: 'app-inbox-mail',
  templateUrl: './inbox-mail.component.html',
  styleUrls: ['./inbox-mail.component.css']
})
export class InboxMailComponent implements OnInit {
  mails: MailList;

  mail: Mail;

  @Output() originalMail = new EventEmitter<Mail>();

  constructor(
    private playerService: PlayerService,
    private toastService: ToastService) {
    this.mails = new MailList();
    this.mail = new Mail();
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(this.mails.getNumberOfMails()).subscribe({
        next: (result: Mail[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
                if (value.whensent !== null) {
                  value.whensent = value.whensent.replace('[UTC]', '')
                }
              }
            );
            this.mails.addAll(result);
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
    this.originalMail.emit(mail);
    return false;
  }

  replyAllMail(mail: Mail) {
    const newmail = new Mail(mail);
    newmail.name = newmail.name + ', ' + newmail.toname;
    this.originalMail.emit(newmail);
    return false;
  }

  public setMail(mail: Mail): void {
    this.mail = mail;
  }

  public deleteMail(mail: Mail): boolean {
    this.playerService.deleteMail(mail).subscribe({
        next: (result: any) => { // on success
          this.mails.delete(mail);
          this.toastService.show('Mail deleted.', {
            delay: 3000,
            autohide: true,
            headertext: 'Deleted...'
          });
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
    this.mails.getSelectedMails().forEach((mail) => this.deleteMail(mail));
    return false;
  }

  public previous(): boolean {
    this.mails.previous();
    return false;
  }

  public setPage(page: number): boolean {
    this.mails.setPage(page);
    return false;
  }

  public toggleMail(mail: Mail) {
    mail.selected = !mail.selected;
    return false;
  }

  public toggleAll(): boolean {
    this.mails.toggleAll();
    return false;
  }

  public next(): boolean {
    if (this.mails.page !== this.mails.getNumberOfPages() - 1) {
      this.mails.next();
      return true;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(this.mails.getNumberOfMails()).subscribe({
        next: (result: Mail[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
                if (value.whensent !== null) {
                  value.whensent = value.whensent.replace('[UTC]', '')
                }
              }
            );
            this.mails.addAll(result);
            this.mails.next();
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


}
