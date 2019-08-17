import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';


import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BlogsComponent } from './blogs/blogs.component';
import { TemplatesComponent } from './templates/templates.component';
import { ErrorsComponent } from './errors/errors.component';
import { SystemlogComponent } from './systemlog/systemlog.component';

@NgModule({
  declarations: [
    AppComponent,
    BlogsComponent,
    TemplatesComponent,
    ErrorsComponent,
    SystemlogComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
