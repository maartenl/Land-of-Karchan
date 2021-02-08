import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';

import { CookieService } from 'ngx-cookie-service';
import { ChristmasUtils } from './christmas.utils';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  darkmode: boolean = false;

  collapsed = true;

  constructor(
    private cookieService: CookieService,
    private router: Router) {
  }

  ngOnInit(): void {
    // check darkmode
    this.darkmode = this.cookieService.check('karchandarkmode');
    if (this.darkmode) {
      document.getElementById('pagestyle')?.setAttribute('href', 'assets/css/bootstrap.darkmode.min.css');
    }
  }

  getFavicon(): string {
    if (ChristmasUtils.isChristmas()) {
      return 'assets/images/santadragon.gif';
    }
    return 'assets/images/dragon.gif';
  }

  getHelpLink(): string {
    const url = this.router.url.substring(1);
    if (url.indexOf('/') === -1) {
      if (url === '') {
        // this.router.url is for example /
        return '/wiki/Admin Pages.html';
      }
      // this.router.url is for example /commands
      return '/wiki/Admin' + url + '.html';
    }
    // this.router.url is for example /commands/15
    const wikiUrl = url.substring(0, url.indexOf('/'));
    return '/wiki/Admin' + wikiUrl + '.html';
  }

}
