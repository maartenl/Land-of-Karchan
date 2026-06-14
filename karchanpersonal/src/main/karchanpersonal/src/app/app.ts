import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { ChristmasUtils } from './christmas.utils';
import { Toasts } from './toasts/toasts';
import { Errors } from './errors/errors';
import { Game } from './game/game';
import {environment} from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, Toasts, Errors, Game],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  protected readonly title = signal('Land of Karchan' + (environment.production ? "" : " (DEV)"));

  collapsed = true;

  getFavicon(): string {
    if (ChristmasUtils.isChristmas()) {
      return 'assets/images/santadragon.gif';
    }
    return 'assets/images/dragon.gif';
  }
}
