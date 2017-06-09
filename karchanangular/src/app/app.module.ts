import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { 
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { PlayerSettingsComponent } from './player-settings/player-settings.component';

import { PlayerService } from './player.service';
import { ErrorsService } from './errors.service';
import { ErrorsComponent } from './errors/errors.component';
import { GameComponent } from './game/game.component';
import { MailComponent } from './mail/mail.component';

@NgModule({
  declarations: [
    AppComponent,
    PlayerSettingsComponent,
    ErrorsComponent,
    GameComponent,
    MailComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule
  ],
  providers: [
    PlayerService,
    ErrorsService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
