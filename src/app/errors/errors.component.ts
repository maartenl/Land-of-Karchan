import { Component, OnInit } from '@angular/core';

import { ErrorsService} from 'app/errors.service';
import { Error } from './error.model';

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.css']
})
export class ErrorsComponent implements OnInit {

  errors: Error[] = [];
  
  constructor(private errorsService: ErrorsService) { }

  ngOnInit() {
    console.log("ErrorsCompoent -> ngOnInit");
    this.errorsService.setListener((error: Error) => this.addError(error));
  }

  public ifError(): boolean {
    return this.errors.length != 0;
  }

  public removeError(error: Error): void {
    let index = this.errors.indexOf(error);
    if (index === -1) {
      // not found. do nothing.
      return;
    }
    this.errors.splice(index, 1);
  }

  public addError(error: Error): void {
    console.log("ErrorsComponent.addError ", error);
    console.log(this.errors);
    this.errors.push(error);
    console.log(this.errors);
  }
}
