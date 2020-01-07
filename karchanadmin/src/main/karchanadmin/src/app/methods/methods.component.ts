import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

import { MethodsRestService } from '../methods-rest.service';
import { Method } from './method.model';
import { Command } from '../commands/command.model';
import { AdminComponent } from '../admin/admin.component';

@Component({
  selector: 'app-methods',
  templateUrl: './methods.component.html',
  styleUrls: ['./methods.component.css']
})
export class MethodsComponent extends AdminComponent<Method, string> implements OnInit {

  commands: Command[] = [];

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
    this.getItems();
  }

  constructor(
    private methodsRestService: MethodsRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder) {
    super();
    this.setForm();
    this.item = this.makeItem();
    this.getItems();
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
    const name: string = this.route.snapshot.paramMap.get('name');
    if (name === undefined || name === null) {
      return;
    }
    this.setItemById(name);
  }

  getRestService(): MethodsRestService {
    return this.methodsRestService;
  }

  setForm(item?: Method) {
    const object = item === undefined ? {
      name: '',
      src: null,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  setItemById(name: string) {
    this.methodsRestService.get(name).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.item = data;
          this.setForm(data);
        }
      }
    });
    this.methodsRestService.getCommands(name).subscribe({
      next: (data) => {
        if (data !== undefined) { this.commands = data; }
      }
    });
    return false;
  }

  getForm(): Method {
    const formModel = this.form.value;

    // return new `Method` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const saveMethod: Method = new Method({
      name: formModel.name as string,
      src: formModel.src as string,
      creation: this.item.creation as string,
      owner: this.item.owner as string
    });
    return saveMethod;
  }

  getItems() {
    this.methodsRestService.getAll().subscribe({
      next: data => {
        const ownerFilter = method => this.searchTerms.owner === undefined ||
          this.searchTerms.owner === null ||
          this.searchTerms.owner === method.owner;
        this.items = data.filter(ownerFilter);
      }
    });
  }

  makeItem(): Method {
    return new Method();
  }

}

