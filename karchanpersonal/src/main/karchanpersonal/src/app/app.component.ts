import { Component, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { CookieService } from 'ngx-cookie-service';
import { ChristmasUtils } from './christmas.utils';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  production: boolean;

  darkmode: boolean;

  collapsed = true;

  constructor(
    private cookieService: CookieService) {
    this.production = environment.production;
    if (window.console) { console.log('Production: ' + this.production); }
  }

  ngOnInit(): void {
    this.darkmode = this.cookieService.check('karchandarkmode');
    if (this.darkmode) {
      document.getElementById('pagestyle').setAttribute('href', 'assets/css/bootstrap.darkmode.min.css');
    }
  }

  getFavicon(): string {
    if (ChristmasUtils.isChristmas()) {
      return 'assets/images/santadragon.gif';
    }
    return 'assets/images/dragon.gif';
  }
}
