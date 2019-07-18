import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GuildComponent } from './guild/guild.component';
import { MailComponent } from './mail/mail.component';
import { PlayerSettingsComponent } from './player-settings/player-settings.component';


const routes: Routes = [
  { path: 'settings', component: PlayerSettingsComponent },
  { path: 'mail', component: MailComponent },
  { path: 'guild', component: GuildComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
