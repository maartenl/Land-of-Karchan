import {Injectable} from '@angular/core';
import {MailList} from './mails/mail.model';
import {Logger} from './consolelog.service';

@Injectable({
  providedIn: 'root',
})
export class MailService {

  inboxList = new MailList();
  sentList = new MailList();

  constructor() {
    Logger.logEntering("MailService.constructor");
  }


}
