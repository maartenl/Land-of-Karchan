import {inject, Injectable} from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient} from '@angular/common/http';
import {ErrorsService} from './errors.service';
import {ToastService} from './toast.service';
import {urls} from './urls';
import {catchError, map, Observable, ReplaySubject, share} from 'rxjs';
import {Guild, Guildmember} from './guilds/guild.model';
import {AdminRestService} from './admin/admin-rest.service';

@Injectable({
  providedIn: 'root',
})
export class GuildsRestService extends AdminRestService<Guild, string> {
  url: string = urls.GUILDS_URL;

  http = inject(HttpClient);
  errorsService = inject(ErrorsService);
  toastService = inject(ToastService);

  cache$: Observable<Guild[]> | null = null;

  get(id: string): Observable<Guild> {
    return this.http.get<Guild>(this.url + '/' + id + environment.postfix)
      .pipe(
        map(item => new Guild(item)),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getAll(): Observable<Guild[]> {
    if (this.cache$) {
      return this.cache$;
    }
    this.toastService.showRetrieving("Retrieving all guilds.",  "Loading...");
    this.cache$ = this.http.get<Guild[]>(this.url + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Guild>();
          items.forEach(item => newItems.push(new Guild(item)));
          return newItems;
        }),
        share({
          connector: () => new ReplaySubject(1),
          resetOnError: false,
          resetOnComplete: false,
          resetOnRefCountZero: false
        }),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
    return this.cache$;
  }

  clearCache() {
    this.cache$ = null;
  }

  delete(guild: Guild): Observable<any> {
    return this.http.delete(this.url + '/' + guild.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  update(guild: Guild) {
    // update
    return this.http.put<Guild[]>(this.url + '/' + guild.name, guild)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  create(guild: Guild) {
    // new
    return this.http.post(this.url, guild)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  getGuildmembers(guild: string): Observable<Guildmember[]> {
    return this.http.get<Guildmember[]>(this.url + "/" + guild + "/guildmembers" + environment.postfix)
      .pipe(
        map(items => {
          const newItems = new Array<Guildmember>();
          items.forEach(item => newItems.push(new Guildmember(item)));
          return newItems;
        }),
        share({
          connector: () => new ReplaySubject(1),
          resetOnError: false,
          resetOnComplete: false,
          resetOnRefCountZero: false
        }),
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

}
