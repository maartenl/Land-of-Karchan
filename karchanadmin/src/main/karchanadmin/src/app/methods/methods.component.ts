import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

import { MethodsRestService } from '../methods-rest.service';
import { Method } from './method.model';
import { Command } from '../commands/command.model';

@Component({
  selector: 'app-methods',
  templateUrl: './methods.component.html',
  styleUrls: ['./methods.component.css']
})
export class MethodsComponent implements OnInit {

  methods: Method[];

  commands: Command[] = [];

  method: Method;

  form: FormGroup;

  SearchTerms = class {
    owner: string;
  };

  searchTerms = new this.SearchTerms();

  updateOwner(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.owner = value;
    this.methodsRestService.getMethods().subscribe({
      next: data => {
        this.methods = data;
      }
    });
  }

  constructor(
    private methodsRestService: MethodsRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder) {
    this.createForm();
    this.method = new Method();
    this.methodsRestService.getMethods().subscribe({
      next: data => {
        this.methods = data;
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
    const name: string = this.route.snapshot.paramMap.get('name');
    if (name === undefined || name === null) {
      return;
    }
    this.setMethodById(name);
  }

  createForm() {
    this.form = this.formBuilder.group({
      name: '',
      src: null,
      owner: null
    });
  }

  resetForm() {
    this.form.reset({
      name: '',
      src: null,
      owner: null
    });
  }

  public cancel(): void {
    this.resetForm();
    this.method = new Method();
  }

  isActive(method: Method) {
    if (method === undefined) {
      return '';
    }
    if (!this.isMethodSelected()) {
      return '';
    }
    return (this.method.name === method.name) ? 'table-active' : '';
  }

  setMethodById(name: string) {
    console.log('setmethodbyid' + name);
    this.methodsRestService.getMethod(name).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setMethod(data); }
      }
    });
    this.methodsRestService.getCommands(name).subscribe({
      next: (data) => {
        if (data !== undefined) { this.commands = data; }
      }
    });
    return false;
  }

  private setMethod(method: Method) {
    this.method = method;
    this.form.reset({
      name: method.name,
      owner: method.owner,
      src: method.src
    });
  }

  public deleteMethod(): void {
    if (window.console) {
      console.log('deleteMethod ' + this.method.name);
    }
    this.methodsRestService.deleteMethod(this.method).subscribe(
      (result: any) => { // on success
        this.methods = this.methods.filter((bl) => bl === undefined || bl.name !== this.method.name);
        this.methods = [...this.methods];
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public createMethod(): void {
    if (window.console) {
      console.log('createMethod');
    }
    const method = this.prepareSave();
    this.methodsRestService.createMethod(method).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('create the method ' + method);
        }
        this.methods.push(method);
        this.methods = [...this.methods];
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
    return;
  }

  public updateMethod(): void {
    if (window.console) {
      console.log('updateMethod');
    }
    const method = this.prepareSave();
    const index = this.methods.findIndex(mth => mth !== null && mth.name === method.name);
    if (window.console) {
      console.log('updateMethod' + index);
    }
    this.methodsRestService.updateMethod(method).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('update the method ' + method);
        }
        if (method.name !== undefined) {
          this.methods[index] = method;
        }
        this.methods = [...this.methods];
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  prepareSave(): Method {
    const formModel = this.form.value;

    // return new `Method` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const saveMethod: Method = {
      name: formModel.name as string,
      src: formModel.src as string,
      creation: this.method.creation as string,
      owner: this.method.owner as string
    };
    return saveMethod;
  }

  disownMethod() {
    if (!this.isMethodSelected()) {
      return;
    }
    this.method.owner = null;
  }

  isMethodSelected() {
    return this.method !== undefined && this.method !== null;
  }

  refresh() {
    this.methodsRestService.clearCache();
    this.methodsRestService.getMethods().subscribe({
      next: data => {
        this.methods = data;
      }
    });
  }
}

