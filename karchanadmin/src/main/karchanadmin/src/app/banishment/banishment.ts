import { Component } from '@angular/core';
import {
  NgbNavContent,
  NgbNav,
  NgbNavItem,
  NgbNavItemRole,
  NgbNavLinkBase,
  NgbNavOutlet, NgbNavLink,
} from '@ng-bootstrap/ng-bootstrap/nav';
import {Bannednames} from './bannednames/bannednames';
import {Banned} from './banned/banned';
import {Sillynames} from './sillynames/sillynames';
import {Unbanned} from './unbanned/unbanned';

@Component({
  selector: 'app-banishment',
  imports: [NgbNavContent, NgbNav, NgbNavItem, NgbNavItemRole, NgbNavLink, NgbNavLinkBase, NgbNavOutlet, Bannednames, Banned, Sillynames, Unbanned],  templateUrl: './banishment.html',
  styleUrl: './banishment.css',
})
export class Banishment {
  active = 1;
}
