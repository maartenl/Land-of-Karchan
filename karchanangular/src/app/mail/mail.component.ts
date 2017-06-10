import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Mail, MailList } from './mail.model';
import { PlayerService } from 'app/player.service';

@Component({
  selector: 'app-mail',
  templateUrl: './mail.component.html',
  styleUrls: ['./mail.component.css']
})
export class MailComponent implements OnInit {
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
    this.playerService.getMail(0).subscribe(
      (result: any) => { // on success
        this.mails = result;
      },
      (err: any) => { // error
        // console.log("error", err);
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

  public setMail(mail: Mail): void {
    this.mail = mail;
  }

  public send(): void {
    let newMail = this.prepareSaveMail();
    this.playerService.sendMail(newMail).subscribe();
  }

  prepareSaveMail(): Mail {
    const formModel = this.mailForm.value;

    // return new `Mail` object
    const mail: Mail = new Mail();
    mail.subject = formModel.subject as string;
    mail.toname = formModel.toname as string;
    mail.body = formModel.body as string;
    return mail;
  }

  public cancel(): void {
    this.resetForm();
  }

}
