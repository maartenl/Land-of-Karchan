import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BlogsComponent } from './blogs/blogs.component';
import { TemplatesComponent } from './templates/templates.component';
import { SystemlogComponent } from './systemlog/systemlog.component';

const routes: Routes = [
  { path: 'blogs', component: BlogsComponent },
  { path: 'templates', component: TemplatesComponent },
  { path: 'systemlog', component: SystemlogComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
