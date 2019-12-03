import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BlogsComponent } from './blogs/blogs.component';
import { TemplatesComponent } from './templates/templates.component';
import { SystemlogComponent } from './systemlog/systemlog.component';
import { RoomsComponent } from './rooms/rooms.component';
import { MethodsComponent } from './methods/methods.component';
import { CommandsComponent } from './commands/commands.component';

const routes: Routes = [
  { path: 'blogs', component: BlogsComponent },
  { path: 'templates', component: TemplatesComponent },
  { path: 'systemlog', component: SystemlogComponent },
  { path: 'rooms/:id', component: RoomsComponent },
  { path: 'rooms', component: RoomsComponent },
  { path: 'methods/:name', component: MethodsComponent },
  { path: 'methods', component: MethodsComponent },
  { path: 'commands', component: CommandsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
