import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

import {ChristmasUtils} from './christmas.utils';
import {ThemeService} from "./theme.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  collapsed = true;

  constructor(private themeService: ThemeService,
              private router: Router) {
  }

  ngOnInit(): void {
    if (window.console) {
      console.log('Is darkmode active: ' + this.themeService.isDarkThemeActive());
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
