import { Component, OnInit } from '@angular/core';

import { ErrorsService} from '../errors.service';
import { ErrorMessage } from './errormessage.model';

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.css']
})
export class ErrorsComponent implements OnInit {
  errors: ErrorMessage[] = [] = new Array<ErrorMessage>(0);

  constructor(private errorsService: ErrorsService) { }

  ngOnInit() {
    this.errorsService.setListener((error: ErrorMessage) => this.addError(error));
  }

  public ifError(): boolean {
    return this.errors.length !== 0;
  }

  public removeError(error: ErrorMessage): void {
    const index = this.errors.indexOf(error);
    if (index === -1) {
      // not found. do nothing.
      return;
    }
    this.errors.splice(index, 1);
  }

  public addError(error: ErrorMessage): void {
    this.errors.push(error);
  }

}
