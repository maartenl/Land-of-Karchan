import { Component, OnInit, Input } from '@angular/core';
import { PlayerService } from 'src/app/player.service';
import { ToastService } from 'src/app/toast.service';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { Mail } from '../mail.model';

@Component({
  selector: 'app-compose-mail',
  templateUrl: './compose-mail.component.html',
  styleUrls: ['./compose-mail.component.css']
})
export class ComposeMailComponent implements OnInit {

  mailForm: FormGroup;

  @Input()
  set originalMail(mail: Mail) {
    this.mailForm.reset({
      toname: mail.name,
      subject: 'Re: ' + mail.subject,
      body: '\n\n<p>On ' + this.getFullWhen(mail) + ' ' + mail.name + ' wrote:</p><hr/>\n' + mail.body + '\n<hr/>'
    });
  }

  public getFullWhen(mail: Mail): string {
    const date: Date = new Date(mail.whensent);
    return date.toDateString() + ' ' + date.toTimeString();
  }

  constructor(
    private playerService: PlayerService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    this.createForm();
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
    const newMails: Mail[] = this.prepareSaveMail();
    for (const newMail of newMails) {
      this.playerService.sendMail(newMail).subscribe(
        (result: any) => { // on success
          this.resetForm();
          this.toastService.show('New mail sent.', {
            delay: 3000,
            autohide: true,
            headertext: 'Success...'
          });
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
    }
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

}
