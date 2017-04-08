import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { PlayerSettingsComponent } from './player-settings/player-settings.component';

@NgModule({
  declarations: [
    AppComponent,
    PlayerSettingsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
