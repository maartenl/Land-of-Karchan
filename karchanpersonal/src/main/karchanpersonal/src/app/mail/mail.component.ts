import { Component, OnInit } from '@angular/core';

import { Mail } from './mail.model';

@Component({
  selector: 'app-mail',
  templateUrl: './mail.component.html',
  styleUrls: ['./mail.component.css']
})
export class MailComponent implements OnInit {

  /**
   * The mail to be used to reply to.
   */
  originalMail: Mail;

  constructor() {
    this.originalMail = new Mail();
  }

  ngOnInit() {
  }

  setOriginalMail(mail: Mail) {
    this.originalMail = mail;
  }
}
