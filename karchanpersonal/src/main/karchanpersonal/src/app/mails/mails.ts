import {Component} from '@angular/core';
import {
  NgbNav,
  NgbNavContent,
  NgbNavItem,
  NgbNavItemRole, NgbNavLinkBase,
  NgbNavLinkButton,
  NgbNavOutlet
} from '@ng-bootstrap/ng-bootstrap';
import {SentMail} from './sent-mail/sent-mail';
import {InboxMail} from './inbox-mail/inbox-mail';

@Component({
  selector: 'app-mails',
  imports: [SentMail, InboxMail,
    NgbNavContent,
    NgbNav,
    NgbNavItem,
    NgbNavItemRole,
    NgbNavLinkButton,
    NgbNavLinkBase,
    NgbNavOutlet,],
  templateUrl: './mails.html',
  styleUrl: './mails.css',
})
export class Mails {}
