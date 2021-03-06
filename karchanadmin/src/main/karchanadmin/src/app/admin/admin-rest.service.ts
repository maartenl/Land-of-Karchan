import { Observable } from 'rxjs';

/**
 * @param T the object
 * @param U the primary key for the object, usually number or string.
 */
export interface AdminRestService<T, U> {

  get(id: U): Observable<T>;

  getAll(descriptionSearch: string): Observable<T[]>;

  clearCache(): void;

  delete(item: T): Observable<any>;

  update(item: T): any;

  create(item: T): any;

}
