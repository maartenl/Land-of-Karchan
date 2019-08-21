import { Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  darkmode: boolean;

  constructor(private cookieService: CookieService) {
  }

  ngOnInit(): void {
    // check darkmode
    this.darkmode = this.cookieService.check('karchandarkmode');
    if (this.darkmode) {
      document.getElementById('pagestyle').setAttribute('href', 'assets/css/bootstrap.darkmode.min.css');
    }
  }

}
