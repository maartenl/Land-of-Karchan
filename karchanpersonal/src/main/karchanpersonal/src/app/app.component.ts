import { Component, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  production: boolean;

  darkmode: boolean;

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

  //  isChristmas(currentDate: Date = new Date()): boolean {
  isChristmas(currentDate: Date = new Date(2019, 11, 27)): boolean {
    const beforeChristmas = new Date(currentDate.getFullYear(), 11, 7).getTime();
    const afterChristmas = new Date(currentDate.getFullYear() + 1, 0, 6).getTime();
    return beforeChristmas < currentDate.getTime() && afterChristmas > currentDate.getTime();
  }

  getFavicon(): string {
    if (this.isChristmas()) {
      return 'assets/images/santadragon.gif';
    }
    return 'assets/images/dragon.gif';
  }
}
