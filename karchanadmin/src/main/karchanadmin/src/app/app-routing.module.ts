import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BlogsComponent} from './blogs/blogs.component';
import {TemplatesComponent} from './templates/templates.component';
import {SystemlogComponent} from './systemlog/systemlog.component';
import {RoomsComponent} from './rooms/rooms.component';
import {MethodsComponent} from './methods/methods.component';
import {AreasComponent} from './areas/areas.component';
import {CommandsComponent} from './commands/commands.component';
import {ItemsComponent} from './items/items.component';
import {BanComponent} from './ban/ban.component';
import {ManpagesComponent} from './manpages/manpages.component';
import {EventsComponent} from './events/events.component';
import {WorldattributesComponent} from './worldattributes/worldattributes.component';
import {AttributesComponent} from './attributes/attributes.component';
import {CharactersComponent} from './characters/characters.component';
import {BoardsComponent} from './boards/boards.component';
import {GuildsComponent} from './guilds/guilds.component';

const routes: Routes = [
  {path: 'blogs', component: BlogsComponent},
  {path: 'templates', component: TemplatesComponent},
  {path: 'systemlog', component: SystemlogComponent},
  {path: 'rooms/:id', component: RoomsComponent},
  {path: 'rooms', component: RoomsComponent},
  {path: 'areas/:id', component: AreasComponent},
  {path: 'areas', component: AreasComponent},
  {path: 'methods/:name', component: MethodsComponent},
  {path: 'methods', component: MethodsComponent},
  {path: 'commands/:id', component: CommandsComponent},
  {path: 'commands', component: CommandsComponent},
  {path: 'items/:id', component: ItemsComponent},
  {path: 'items', component: ItemsComponent},
  {path: 'ban', component: BanComponent},
  {path: 'manpages', component: ManpagesComponent},
  {path: 'events', component: EventsComponent},
  {path: 'attributes/:name', component: AttributesComponent},
  {path: 'attributes', component: AttributesComponent},
  {path: 'worldattributes', component: WorldattributesComponent},
  {path: 'characters/:name', component: CharactersComponent},
  {path: 'characters', component: CharactersComponent},
  {path: 'boards', component: BoardsComponent},
  {path: 'guilds', component: GuildsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
