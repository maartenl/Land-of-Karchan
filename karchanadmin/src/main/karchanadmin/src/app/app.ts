import {Component, inject, signal} from '@angular/core';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {ChristmasUtils} from './christmas.utils';
import {Toasts} from './toasts/toasts';
import {Errors} from './errors/errors';
import {Currentadmin} from './currentadmin/currentadmin';

import { AllCommunityModule, ModuleRegistry } from "ag-grid-community";
import {environment} from '../environments/environment';

ModuleRegistry.registerModules([AllCommunityModule]);

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLinkActive, Toasts, Errors, Currentadmin, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  private router = inject(Router)

  protected readonly title = signal('Land of Karchan' + (environment.production ? "" : " (DEV)"));

  collapsed = signal(true);

  getFavicon(): string {
    if (ChristmasUtils.isChristmas()) {
      return 'assets/images/santadragon.gif';
    }
    return 'assets/images/dragon.gif';
  }

  toggleCollapsed() {
    this.collapsed.update(value => !value);
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
