import { Observable } from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {ErrorsService} from '../errors.service';

/**
 * @param T the object
 * @param U the primary key for the object, usually number or string.
 */
export abstract class AdminRestService<T, U> {

  abstract cache$: Observable<T[]> | null;

  abstract errorsService: ErrorsService;

  abstract get(id: U): Observable<T>;

  abstract getAll(): Observable<T[]>;

  abstract clearCache(): void;

  abstract delete(item: T): Observable<any>;

  abstract update(item: T): any;

  abstract create(item: T): any;

  /**
   * Handles error, delivers them to the errorService.
   * @param error the error message received from the HTTP call
   * @param ignore which states can we choose to ignore?
   */
  protected handleError(error: HttpErrorResponse, ignore?: string[]) {
  this.errorsService.addHttpError(error, ignore);
}
}
