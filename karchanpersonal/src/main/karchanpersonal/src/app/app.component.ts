import { Component, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { CookieService } from 'ngx-cookie-service';
import { PlayerService } from './player.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  production: boolean;

  darkmode: boolean;

  constructor(
    private cookieService: CookieService,
    private playerService: PlayerService) {
    this.production = environment.production;
    if (window.console) {console.log('Production: ' + this.production);}
  }

  ngOnInit(): void {
    this.darkmode = this.cookieService.check('karchandarkmode');
    if (this.darkmode) {
      document.getElementById('pagestyle').setAttribute('href', 'assets/css/bootstrap.darkmode.min.css');
    }
  }

  public playGame(): void {
    this.playerService.enterGame()
      .subscribe(
        (result: any) => { // on success
          if (window.location !== window.parent.location) {
            window.parent.location.href = '/game/play.html';
          }
          window.location.href = '/game/play.html';
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }
}
