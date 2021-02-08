import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DefaultHttpInterceptor } from './http.interceptor';
import { MockHttpInterceptor } from './http.mock.interceptor';
import { BlogsComponent } from './blogs/blogs.component';
import { TemplatesComponent } from './templates/templates.component';
import { ErrorsComponent } from './errors/errors.component';
import { SystemlogComponent } from './systemlog/systemlog.component';
import { RoomsComponent } from './rooms/rooms.component';

import { ScrollingModule } from '@angular/cdk/scrolling';
import { MethodsComponent } from './methods/methods.component';
import { CommandsComponent } from './commands/commands.component';
import { ToastComponent } from './toast/toast.component';
import { AreasComponent } from './areas/areas.component';
import { ItemsComponent } from './items/items.component';
import { BanComponent } from './ban/ban.component';
import { BannedComponent } from './ban/banned/banned.component';
import { BannednamesComponent } from './ban/bannednames/bannednames.component';
import { SillynamesComponent } from './ban/sillynames/sillynames.component';
import { UnbannedComponent } from './ban/unbanned/unbanned.component';
import { ManpagesComponent } from './manpages/manpages.component';
import { EventsComponent } from './events/events.component';
import { WorldattributesComponent } from './worldattributes/worldattributes.component';
import { CharactersComponent } from './characters/characters.component';
import { environment } from 'src/environments/environment';
import { BoardsComponent } from './boards/boards.component';
import { GuildsComponent } from './guilds/guilds.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

export const isMock = environment.mock;

@NgModule({
  declarations: [
    AppComponent,
    BlogsComponent,
    TemplatesComponent,
    ErrorsComponent,
    SystemlogComponent,
    RoomsComponent,
    MethodsComponent,
    CommandsComponent,
    ToastComponent,
    AreasComponent,
    ItemsComponent,
    BanComponent,
    BannedComponent,
    BannednamesComponent,
    SillynamesComponent,
    UnbannedComponent,
    ManpagesComponent,
    EventsComponent,
    WorldattributesComponent,
    CharactersComponent,
    BoardsComponent,
    GuildsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    ScrollingModule,
    NgbModule,
    NoopAnimationsModule
  ],
  providers: [CookieService, {
    provide: HTTP_INTERCEPTORS,
    useClass: isMock ? MockHttpInterceptor : DefaultHttpInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
