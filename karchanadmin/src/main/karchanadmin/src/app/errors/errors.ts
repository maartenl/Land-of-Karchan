import {Component, inject, signal} from '@angular/core';
import {ErrorsService} from '../errors.service';
import {ErrorMessage} from './errormessage.model';

@Component({
  selector: 'app-errors',
  imports: [],
  templateUrl: './errors.html',
  styleUrl: './errors.css',
})
export class Errors {
  private errorsService = inject(ErrorsService);

  errors = signal<ErrorMessage[]>([]);

  constructor() {
  }

  public ifError(): boolean {
    return this.errors().length !== 0;
  }

  public removeError(error: ErrorMessage): void {
    const index = this.errors().indexOf(error);
    if (index === -1) {
      // not found. do nothing.
      return;
    }
    this.errors.update(errors => errors.splice(index, 1));
  }

  public addError(error: ErrorMessage): void {
    this.errors.update(errors => {
      errors.push(error);
      return errors;
    });
  }
}
