import { Component } from '@angular/core';
import { environment } from '../environments/environment';

export enum Tab {
  Game, Settings, Mail, Guild
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentTab: Tab = Tab.Game;
  production: boolean;

  Tab: Tab;

  constructor() {
    this.production = environment.production;
    if (window.console) {console.log('Production: ' + this.production);}
  }

  setGameTabActive() {
    this.currentTab = Tab.Game;
  }

  isGameTabActive() {
    return this.currentTab === Tab.Game;
  }

  setSettingsTabActive() {
    this.currentTab = Tab.Settings;
  }

  isSettingsTabActive() {
    return this.currentTab === Tab.Settings;
  }

  setMailTabActive() {
    this.currentTab = Tab.Mail;
  }

  isMailTabActive() {
    return this.currentTab === Tab.Mail;
  }

  setGuildTabActive() {
    this.currentTab = Tab.Guild;
  }

  isGuildTabActive() {
    return this.currentTab === Tab.Guild;
  }
}
