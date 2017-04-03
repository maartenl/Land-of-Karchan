import { Component } from '@angular/core';

export enum Tab {
  Settings, Mail, Guild
}


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentTab : Tab = Tab.Settings;

  Tab: Tab;

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
