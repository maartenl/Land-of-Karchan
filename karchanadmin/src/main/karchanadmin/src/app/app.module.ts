import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';

import { CookieService } from 'ngx-cookie-service';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BlogsComponent } from './blogs/blogs.component';
import { TemplatesComponent } from './templates/templates.component';
import { ErrorsComponent } from './errors/errors.component';
import { SystemlogComponent } from './systemlog/systemlog.component';
import { RoomsComponent } from './rooms/rooms.component';

import { ScrollingModule } from '@angular/cdk/scrolling';
import { MethodsComponent } from './methods/methods.component';

@NgModule({
  declarations: [
    AppComponent,
    BlogsComponent,
    TemplatesComponent,
    ErrorsComponent,
    SystemlogComponent,
    RoomsComponent,
    MethodsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    ScrollingModule
  ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule { }
