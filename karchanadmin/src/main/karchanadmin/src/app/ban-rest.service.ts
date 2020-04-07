import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';

import { environment } from '../environments/environment';

import { ErrorsService } from './errors.service';
import { BannedIP } from './ban/banned.model';
import { Bannedname } from './ban/bannednames.model';

@Injectable({
  providedIn: 'root'
})
export class BanRestService {

  constructor(
    private http: HttpClient,
    private errorsService: ErrorsService) {
  }

  public getBannedIPs(): Observable<any> {
    return this.http.get<BannedIP[]>(environment.BAN_URL + '/bannedips')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  deleteBannedIP(banned: BannedIP): Observable<any> {
    return this.http.delete(environment.BAN_URL + '/bannedips/' + banned.address)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  createBannedIP(banned: BannedIP) {
    // new
    return this.http.post(environment.BAN_URL + '/bannedips', banned)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  public getBannednames(): Observable<any> {
    return this.http.get<Bannedname[]>(environment.BAN_URL + '/bannednames')
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  deleteBannedname(banned: Bannedname): Observable<any> {
    return this.http.delete(environment.BAN_URL + '/bannednames/' + banned.name)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  createBannedname(banned: Bannedname) {
    // new
    return this.http.post(environment.BAN_URL + '/bannednames', banned)
      .pipe(
        catchError(err => {
          this.handleError(err);
          return [];
        })
      );
  }

  /**
   * Handles error, delivers them to the errorService.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  private handleError(error: HttpErrorResponse, ignore?: string[]) {
    this.errorsService.addHttpError(error, ignore);
  }
}
