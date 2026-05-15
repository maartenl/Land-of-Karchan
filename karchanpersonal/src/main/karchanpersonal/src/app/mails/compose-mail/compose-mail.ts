import {Component, inject, input, Input, signal} from '@angular/core';
import {Mail} from '../mail.model';
import {form, FormField} from '@angular/forms/signals';
import {PlayerService} from '../../player.service';
import {ToastService} from '../../toast.service';

export interface MailData {
  toname: string;
  subject: string;
  body: string;
}

@Component({
  selector: 'app-compose-mail',
  imports: [FormField],
  templateUrl: './compose-mail.html',
  styleUrl: './compose-mail.css',
})
export class ComposeMail {

  private playerService = inject(PlayerService);
  private toastService = inject(ToastService);

  mailModel = signal<MailData>({toname: '', subject: '', body: ''})

  mailForm = form(this.mailModel);

  setOriginalMail(mail: Mail | undefined) {
    if (mail === undefined || mail === null) {
      return;
    }
    if (mail.name === null || mail.body === null || mail.subject === null) {
      return;
    }
    this.mailModel.set({
      toname: mail.name,
      subject: 'Re: ' + mail.subject,
      body: '\n\n<p>On ' + this.getFullWhen(mail) + ' ' + mail.name + ' wrote:</p><hr/>\n' + mail.body + '\n<hr/>'
    });
  }

  public getFullWhen(mail: Mail): string {
    if (mail.whensent === null) {
      return '';
    }
    const date: Date = new Date(mail.whensent);
    return date.toDateString() + ' ' + date.toTimeString();
  }

  public onSubmit(event: Event) {
    event.preventDefault();
    const newMail: Mail = this.prepareSaveMail();
    this.playerService.sendMail(newMail).subscribe({
        next: (result: any) => { // on success
          this.resetForm();
          this.toastService.showMessage("New mail sent.", "Success...");
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );
  }

  resetForm() {
    this.mailModel.set({
      toname: '',
      subject: '',
      body: ''
    });
  }

  prepareSaveMail(): Mail {
    const formModel = this.mailModel();
    const names: string = formModel.toname as string;

    // return new `Mail` object
    const mail: Mail = new Mail();
    mail.subject = formModel.subject as string;
    mail.toname = names.trim();
    mail.body = formModel.body as string;
    return mail;
  }

  public cancel(): void {
    this.resetForm();
  }

}
