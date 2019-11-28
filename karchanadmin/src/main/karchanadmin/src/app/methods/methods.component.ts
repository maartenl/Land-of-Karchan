import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

import { MethodsRestService } from '../methods-rest.service';
import { Method } from './method.model';

@Component({
  selector: 'app-methods',
  templateUrl: './methods.component.html',
  styleUrls: ['./methods.component.css']
})
export class MethodsComponent extends DataSource<Method> implements OnInit {

  methods: Method[];

  method: Method;

  form: FormGroup;

  datasource: DataSource<Method>;

  private dataStream;

  constructor(
    private methodsRestService: MethodsRestService,
    private formBuilder: FormBuilder) {
    super();
    this.createForm();
    this.method = new Method();
    this.methodsRestService.getCount().subscribe({
      next: amount => {
        this.methods = Array.from<Method>({ length: amount });
        this.dataStream = new BehaviorSubject<(Method | undefined)[]>(this.methods);
      }
    });
    this.datasource = this;
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
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

  connect(collectionViewer: CollectionViewer): Observable<Method[]> {
    if (window.console) {
      console.log('connect');
    }
    // this.subscription.add(
    collectionViewer.viewChange.subscribe(range => {
      if (this.methods.slice(range.start, range.end).some(x => x === undefined)) {
        if (window.console) {
          console.log('Call restservice');
        }
        this.methodsRestService.getMethods(range.start, range.end + 100).subscribe({
          next: (data) => {
            this.methods.splice(range.start, data.length, ...data);
            this.methods = [...this.methods];
            this.dataStream.next(this.methods);
          }
        });
      }
    });
    return this.dataStream;
  }

  disconnect(collectionViewer: CollectionViewer): void {
    if (window.console) {
      console.log('disconnect');
    }
  }

  isActive(method: Method) {
    if (!this.isMethodSelected()) {
      return '';
    }
    return (this.method.name === method.name) ? 'table-active' : '';
  }

  setMethodById(name: string) {
    console.log('setmethodbyid' + name);
    this.methodsRestService.getMethod(name).subscribe({
      next: (data) => {
        console.log(data);
        if (data !== undefined) { this.setMethod(data); }
      }
    });
    return false;
  }

  setMethod(method: Method) {
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
        // this.methods.push(undefined);
        this.methods = [...this.methods];
        this.dataStream.next(this.methods);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public saveMethod(): void {
    if (window.console) {
      console.log('saveMethod');
    }
    const index = this.methods.indexOf(this.method);
    if (window.console) {
      console.log('saveMethod' + index);
    }
    const method = this.prepareSave();
    this.methodsRestService.updateMethod(method).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('save the method ' + method);
        }
        if (method.name !== undefined) {
          this.methods[index] = method;
        }
        this.methods = [...this.methods];
        this.dataStream.next(this.methods);
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
      name: this.method.name as string,
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
    this.dataStream.next(this.methods);
  }

  isMethodSelected() {
    return this.method !== undefined && this.method !== null;
  }
}
