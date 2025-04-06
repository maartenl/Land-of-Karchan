import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {AngularEditorModule} from '@kolkov/angular-editor';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {CookieService} from 'ngx-cookie-service';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';

import {PlayerService} from './player.service';
import {ErrorsService} from './errors.service';
import {ErrorsComponent} from './errors/errors.component';
import {GameComponent} from './game/game.component';
import {MailComponent} from './mail/mail.component';
import {GuildComponent} from './guild/guild.component';
import {GuildMasterComponent} from './guild/guild-master/guild-master.component';
import {GuildMemberComponent} from './guild/guild-member/guild-member.component';
import {PlayerSettingsComponent} from './player-settings/player-settings.component';
import {WikipagesComponent} from './wikipages/wikipages.component';
import {PicturesComponent} from './pictures/pictures.component';
import {PlayComponent} from './play/play.component';
import {ToastComponent} from './toast/toast.component';
import {ComposeMailComponent} from './mail/compose-mail/compose-mail.component';
import {InboxMailComponent} from './mail/inbox-mail/inbox-mail.component';
import {ShowMailComponent} from './mail/show-mail/show-mail.component';
import {SentMailComponent} from './mail/sent-mail/sent-mail.component';
import {LogonmessageComponent} from './play/logonmessage/logonmessage.component';
import {ChatlogComponent} from './play/chatlog/chatlog.component';
import {ThemeService} from "./theme.service";
import { TextareaAutoresizeDirective } from './textarea-autoresize.directive';

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
    ToastComponent,
    ComposeMailComponent,
    InboxMailComponent,
    ShowMailComponent,
    SentMailComponent,
    LogonmessageComponent,
    ChatlogComponent,
    TextareaAutoresizeDirective
  ],
  bootstrap: [AppComponent],
  imports: [BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    AngularEditorModule,
    NgbModule], providers: [
    PlayerService,
    ThemeService,
    ErrorsService,
    CookieService,
    provideHttpClient(withInterceptorsFromDi())
  ]
})
export class AppModule {
}
