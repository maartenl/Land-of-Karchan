import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

import { MethodsRestService } from '../methods-rest.service';
import { Method } from './method.model';

export class MyDataSource extends DataSource<Method> {
  private dataStream;

  methods: Method[];

  constructor(methods: Method[], private methodsRestService: MethodsRestService, private owner: string) {
    super();
    this.methods = methods;
    this.owner = null;
    this.dataStream = new BehaviorSubject<(Method | undefined)[]>(this.methods);
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
        this.methodsRestService.getMethods(range.start, range.end + 100, this.owner).subscribe({
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

  updateDatastream(methods: Method[]) {
    this.methods = methods;
    this.dataStream.next(methods);
  }

  setOwner(owner: string) {
    this.owner = owner;
  }

}

@Component({
  selector: 'app-methods',
  templateUrl: './methods.component.html',
  styleUrls: ['./methods.component.css']
})
export class MethodsComponent implements OnInit {

  methods: Method[];

  private newMethod: boolean;

  method: Method;

  form: FormGroup;

  datasource: MyDataSource;

  SearchTerms = class {
    owner: string;
  };

  searchTerms = new this.SearchTerms();

  updateOwner(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.owner = value;
    this.methodsRestService.getCount(value).subscribe({
      next: amount => {
        this.methods = Array.from<Method>({ length: amount });
        this.datasource.setOwner(value);
        this.datasource.updateDatastream(this.methods);
      }
    });
  }

  constructor(
    private methodsRestService: MethodsRestService,
    private formBuilder: FormBuilder) {
    this.createForm();
    this.method = new Method();
    this.newMethod = true;
    this.methodsRestService.getCount(null).subscribe({
      next: amount => {
        this.methods = Array.from<Method>({ length: amount });
        this.datasource = new MyDataSource(this.methods, this.methodsRestService, null);
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }

  createForm() {
    this.newMethod = true;
    this.form = this.formBuilder.group({
      name: '',
      src: null,
      owner: null
    });
  }

  resetForm() {
    this.newMethod = true;
    this.form.reset({
      name: '',
      src: null,
      owner: null
    });
  }

  public cancel(): void {
    this.newMethod = true;
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
    return false;
  }

  setMethod(method: Method) {
    this.newMethod = false;
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
        this.datasource.updateDatastream(this.methods);
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
    if (this.newMethod) {
      this.methodsRestService.createMethod(method).subscribe(
        (result: any) => { // on success
          if (window.console) {
            console.log('create the method ' + method);
          }
          if (method.name !== undefined) {
            this.methods[index] = method;
          }
          this.methods = [...this.methods];
          this.datasource.updateDatastream(this.methods);
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
      return;
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
        this.datasource.updateDatastream(this.methods);
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
    this.datasource.updateDatastream(this.methods);
  }

  isMethodSelected() {
    return this.method !== undefined && this.method !== null;
  }
}

