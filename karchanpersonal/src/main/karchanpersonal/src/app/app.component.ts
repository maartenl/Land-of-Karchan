import {Component, OnInit} from '@angular/core';
import {environment} from '../environments/environment';
import {ChristmasUtils} from './christmas.utils';
import {ThemeService} from "./theme.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  production: boolean = true;

  collapsed = true;

  constructor(private themeService: ThemeService) {
    this.production = environment.production;
    if (window.console) { console.log('Production: ' + this.production); }
  }

  ngOnInit(): void {
    if (window.console) { console.log('Is darkmode active: ' + this.themeService.isDarkThemeActive()); }

  }

  getFavicon(): string {
    if (ChristmasUtils.isChristmas()) {
      return 'assets/images/santadragon.gif';
    }
    return 'assets/images/dragon.gif';
  }
}
