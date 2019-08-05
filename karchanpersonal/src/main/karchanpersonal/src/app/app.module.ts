import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { PlayerService } from './player.service';
import { ErrorsService } from './errors.service';
import { ErrorsComponent } from './errors/errors.component';
import { GameComponent } from './game/game.component';
import { MailComponent } from './mail/mail.component';
import { GuildComponent } from './guild/guild.component';
import { GuildMasterComponent } from './guild/guild-master/guild-master.component';
import { GuildMemberComponent } from './guild/guild-member/guild-member.component';
import { PlayerSettingsComponent } from './player-settings/player-settings.component';
import { WikipagesComponent } from './wikipages/wikipages.component';

@NgModule({
  declarations: [
    AppComponent,
    ErrorsComponent,
    GameComponent,
    MailComponent,
    GuildComponent,
    GuildMasterComponent,
    GuildMemberComponent,
    PlayerSettingsComponent,
    WikipagesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [
    PlayerService,
    ErrorsService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
