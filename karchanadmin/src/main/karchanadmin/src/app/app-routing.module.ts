import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BlogsComponent } from './blogs/blogs.component';
import { TemplatesComponent } from './templates/templates.component';
import { SystemlogComponent } from './systemlog/systemlog.component';
import { RoomsComponent } from './rooms/rooms.component';
import { MethodsComponent } from './methods/methods.component';
import { AreasComponent } from './areas/areas.component';
import { CommandsComponent } from './commands/commands.component';
import { ItemsComponent } from './items/items.component';
import { BanComponent } from './ban/ban.component';
import { ManpagesComponent } from './manpages/manpages.component';

const routes: Routes = [
  { path: 'blogs', component: BlogsComponent },
  { path: 'templates', component: TemplatesComponent },
  { path: 'systemlog', component: SystemlogComponent },
  { path: 'rooms/:id', component: RoomsComponent },
  { path: 'rooms', component: RoomsComponent },
  { path: 'areas/:id', component: AreasComponent },
  { path: 'areas', component: AreasComponent },
  { path: 'methods/:name', component: MethodsComponent },
  { path: 'methods', component: MethodsComponent },
  { path: 'commands/:id', component: CommandsComponent },
  { path: 'commands', component: CommandsComponent },
  { path: 'items/:id', component: ItemsComponent },
  { path: 'items', component: ItemsComponent },
  { path: 'ban', component: BanComponent },
  { path: 'manpages', component: ManpagesComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
