import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Mail, MailList } from './mail.model';
import { PlayerService } from '../player.service';

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

  mailForm: FormGroup;

  constructor(private playerService: PlayerService,
              private formBuilder: FormBuilder) {
    this.mails = new MailList();
    this.mail = new Mail();
    this.createForm();
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

  createForm() {
    this.mailForm = this.formBuilder.group({
      toname: '',
      subject: '',
      body: ''
    });
  }

  resetForm() {
    this.mailForm.reset({
      toname: '',
      subject: '',
      body: ''
    });
  }

  replyMail(mail: Mail) {
    this.mailForm.reset({
      toname: mail.name,
      subject: 'Re: ' + mail.subject,
      body: '\n\n<p>On ' + this.getFullWhen(mail) + ' ' + mail.name + ' wrote:</p><hr/>\n' + mail.body + '\n<hr/>'
    });
  }

  public setMail(mail: Mail): void {
    this.mail = mail;
  }

  public send(): void {
    const newMails: Mail[] = this.prepareSaveMail();
    for (const newMail of newMails) {
      this.playerService.sendMail(newMail).subscribe(
        (result: any) => { // on success
          this.resetForm();
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
    }
  }

  public deleteMail(mail: Mail): void {
    this.playerService.deleteMail(mail).subscribe(
      (result: any) => { // on success
        this.mails.delete(mail);
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

  prepareSaveMail(): Mail[] {
    const result: Mail[] = [];
    const formModel = this.mailForm.value;
    const namesFromForm: string = formModel.toname as string;
    if (namesFromForm === null || namesFromForm.trim() === '') {
      return result;
    }
    const names = (namesFromForm).split(',');

    for (const name of names) {
      // return new `Mail` object
      const mail: Mail = new Mail();
      mail.subject = formModel.subject as string;
      mail.toname = name.trim();
      mail.body = formModel.body as string;
      result.push(mail);
    }
    return result;
  }

  public cancel(): void {
    this.resetForm();
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

  public getFullWhen(mail: Mail): string {
    const date: Date = new Date(mail.whensent);
    return date.toDateString() + ' ' + date.toTimeString();
  }
}
