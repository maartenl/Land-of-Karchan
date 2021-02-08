import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { CommandsRestService } from '../commands-rest.service';
import { Command } from './command.model';
import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-commands',
  templateUrl: './commands.component.html',
  styleUrls: ['./commands.component.css']
})
export class CommandsComponent extends AdminComponent<Command, number> implements OnInit {

  form: FormGroup;

  SearchTerms = class {
    owner: string | null = null;
    methodName: string | null = null;
  };

  searchTerms = new this.SearchTerms();

  updateOwnerSearch(value: string) {
    this.searchTerms.owner = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateMethodNameSearch(value: string) {
    this.searchTerms.methodName = value.trim() === '' ? null : value;
    this.getItems();
  }

  constructor(
    private commandsRestService: CommandsRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    const object = {
      callable: true,
      command: '',
      methodName: '',
      room: null,
      owner: null
    };
    this.form = this.formBuilder.group(object);
    this.makeItem();
    this.getItems();
  }

  getItems() {
    this.commandsRestService.getAll()
      .subscribe({
        next: data => {
          const ownerFilter = (command: Command) => this.searchTerms.owner === undefined ||
            this.searchTerms.owner === null ||
            this.searchTerms.owner === command.owner;
          const methodNameFilter = (command: Command) => this.searchTerms.methodName === undefined ||
            this.searchTerms.methodName === null ||
            (command.methodName !== null && command.methodName.includes(this.searchTerms.methodName));
          this.items = data.filter(ownerFilter).filter(methodNameFilter);
        }
      });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
    const id: string | null = this.route.snapshot.paramMap.get('id');
    if (id === undefined || id === null) {
      return;
    }
    const idNumber: number = Number(id);
    if (isNaN(idNumber)) {
      return;
    }
    this.setItemById(idNumber);
  }

  setForm(item?: Command) {
    const object = item === undefined ? {
      callable: true,
      command: '',
      methodName: '',
      room: null,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  setItemById(id: number | undefined | null) {
    if (id === undefined || id === null) {
      return false;
    }
    this.commandsRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setCommand(data); }
      }
    });
    return false;
  }

  private setCommand(command: Command) {
    this.item = command;
    this.form.reset({
      id: command.id,
      callable: command.callable,
      command: command.command,
      methodName: command.methodName,
      room: command.room,
      owner: command.owner
    });
  }

  getForm(): Command {
    const formModel = this.form.value;

    // return new `Command` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const id = this.item === undefined || this.item === null ? null : this.item.id;
    const owner = this.item === undefined || this.item === null ? null : this.item.owner;
    const saveCommand: Command = new Command({
      id,
      callable: formModel.callable as boolean,
      command: formModel.command as string,
      room: formModel.room as number,
      methodName: formModel.methodName as string,
      creation: null,
      owner
    });
    return saveCommand;
  }

  getRestService(): CommandsRestService {
    return this.commandsRestService;
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  makeItem(): Command {
    return new Command();
  }

  sortById() {
    if (window.console) {
      console.log('sortById');
    }
    this.items = this.items.sort((a, b) => {
      const aid = a.id === null ? -1 : a.id;
      const bid = b.id === null ? -1 : b.id;
      return aid - bid;
    });
    this.items = [...this.items];
    return false;
  }

  sortByCommand() {
    if (window.console) {
      console.log('sortByCommand');
    }
    this.items = this.items.sort((a, b) => {
      const acommand = a.command === null ? '' : a.command;
      const bcommand = b.command === null ? '' : b.command;
      return acommand.localeCompare(bcommand);
    });
    this.items = [...this.items];
    return false;
  }

  sortByMethodName() {
    if (window.console) {
      console.log('sortByMethodName');
    }
    this.items = this.items.sort((a, b) => { 
      const amethodName = a.methodName === null ? '' : a.methodName;
      const bmethodName = b.methodName === null ? '' : b.methodName;
      return amethodName.localeCompare(bmethodName); 
    });
    this.items = [...this.items];
    return false;
  }
}

