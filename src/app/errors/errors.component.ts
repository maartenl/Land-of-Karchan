import { Component, OnInit } from '@angular/core';

import { Error } from './error.model';

@Component({
  selector: 'app-errors',
  templateUrl: './errors.component.html',
  styleUrls: ['./errors.component.css']
})
export class ErrorsComponent implements OnInit {

  errors: Error[] = [];
  
  constructor() { }

  ngOnInit() {
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
}
