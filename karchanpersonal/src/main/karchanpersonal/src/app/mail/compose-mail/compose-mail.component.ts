import {Component, Input, OnInit} from '@angular/core';
import {PlayerService} from 'src/app/player.service';
import {ToastService} from 'src/app/toast.service';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Mail} from '../mail.model';

@Component({
  selector: 'app-compose-mail',
  templateUrl: './compose-mail.component.html',
  styleUrls: ['./compose-mail.component.css']
})
export class ComposeMailComponent implements OnInit {

  mailForm: FormGroup;

  @Input()
  set originalMail(mail: Mail) {
    if (mail === undefined || mail === null) {
      return;
    }
    if (mail.name === null || mail.body === null || mail.subject === null) {
      return;
    }
    this.mailForm.reset({
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

  constructor(
    private playerService: PlayerService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    this.mailForm = this.formBuilder.group({
      toname: '',
      subject: '',
      body: ''
    });
  }

  ngOnInit() {
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

  public send(): void {
    const newMail: Mail = this.prepareSaveMail();
    this.playerService.sendMail(newMail).subscribe({
        next: (result: any) => { // on success
          this.resetForm();
          this.toastService.show('New mail sent.', {
            delay: 3000,
            autohide: true,
            headertext: 'Success...'
          });
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );
  }


  prepareSaveMail(): Mail {
    const formModel = this.mailForm.value;
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
