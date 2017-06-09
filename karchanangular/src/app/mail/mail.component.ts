import { Component, OnInit } from '@angular/core';

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

  constructor(private playerService: PlayerService) { 
    this.mails = new MailList();
    this.mail = new Mail();
  }

  ngOnInit() {
    this.playerService.getMail(0) .subscribe(
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

  public setMail(mail: Mail): void {
    this.mail = mail;
  }

}
