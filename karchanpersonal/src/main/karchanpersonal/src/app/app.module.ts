import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { CookieService } from 'ngx-cookie-service';
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
import { PicturesComponent } from './pictures/pictures.component';
import { PlayComponent } from './play/play.component';
import { ToastComponent } from './toast/toast.component';

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
    WikipagesComponent,
    PicturesComponent,
    PlayComponent,
    ToastComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    CKEditorModule,
    NgbModule
  ],
  providers: [
    PlayerService,
    ErrorsService,
    CookieService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
