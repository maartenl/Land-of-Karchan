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
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(this.mails.getNumberOfMails()).subscribe(
      (result: any) => { // on success
        this.mails.addAll(result);
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
    let newMails: Mail[] = this.prepareSaveMail();
    for (let newMail of newMails) {
      this.playerService.sendMail(newMail).subscribe(
        (result: any) => { // on success
          this.resetForm();
        },
        (err: any) => { // error
          // console.log("error", err);
        },
        () => { // on completion
        }
      );
    }
  }

  prepareSaveMail(): Mail[] {
    let result: Mail[] = [];
    const formModel = this.mailForm.value;
    let namesFromForm: string = formModel.toname as string;
    if (namesFromForm === null || namesFromForm.trim() === "") {
      return result;
    }
    let names = (namesFromForm).split(",");

    for (let name of names) {
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
    if (this.mails.page != this.mails.getNumberOfPages() - 1) {
      this.mails.next();
      return;
    }
    // retrieve the next page of mails starting from the last mail in the array
    this.playerService.getMail(this.mails.getNumberOfMails()).subscribe(
      (result: any) => { // on success
        if (result.getNumberOfMails() != 0) {
          this.mails.addAll(result);
          this.mails.next();
        }
      },
      (err: any) => { // error
        // console.log("error", err);
      },
      () => { // on completion
      }
    );
  }

}
