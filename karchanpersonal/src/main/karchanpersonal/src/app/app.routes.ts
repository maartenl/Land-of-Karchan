import { Routes } from '@angular/router';
import { Guilds } from './guilds/guilds';
import { Mails } from './mails/mails';
import { PlayerSettings } from './player-settings/player-settings';
import { Wikipages } from './wikipages/wikipages';
import { Pictures } from './pictures/pictures';
import { Play } from './play/play';

export const routes: Routes = [
  { path: 'settings', component: PlayerSettings },
  { path: 'mail', component: Mails },
  { path: 'guild', component: Guilds },
  { path: 'wikipages/:title', component: Wikipages },
  { path: 'play', component: Play },
  { path: 'pictures', component: Pictures }
];
