import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

import { CommandsRestService } from '../commands-rest.service';
import { Command } from './command.model';

export class MyDataSource extends DataSource<Command> {
  private dataStream;

  commands: Command[];

  constructor(commands: Command[], private commandsRestService: CommandsRestService, private owner: string) {
    super();
    this.commands = commands;
    this.owner = null;
    this.dataStream = new BehaviorSubject<(Command | undefined)[]>(this.commands);
  }

  connect(collectionViewer: CollectionViewer): Observable<Command[]> {
    if (window.console) {
      console.log('connect');
    }
    // this.subscription.add(
    collectionViewer.viewChange.subscribe(range => {
      if (this.commands.slice(range.start, range.end).some(x => x === undefined)) {
        if (window.console) {
          console.log('Call restservice');
        }
        this.commandsRestService.getCommands(range.start, range.end + 100, this.owner).subscribe({
          next: (data) => {
            this.commands.splice(range.start, data.length, ...data);
            this.commands = [...this.commands];
            this.dataStream.next(this.commands);
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

  updateDatastream(commands: Command[]) {
    this.commands = commands;
    this.dataStream.next(commands);
  }

  setOwner(owner: string) {
    this.owner = owner;
  }

}

@Component({
  selector: 'app-commands',
  templateUrl: './commands.component.html',
  styleUrls: ['./commands.component.css']
})
export class CommandsComponent implements OnInit {

  commands: Command[];

  command: Command;

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
    this.commandsRestService.getCount(value).subscribe({
      next: amount => {
        this.commands = Array.from<Command>({ length: amount });
        this.datasource.setOwner(value);
        this.datasource.updateDatastream(this.commands);
      }
    });
  }

  constructor(
    private commandsRestService: CommandsRestService,
    private formBuilder: FormBuilder) {
    this.createForm();
    this.command = new Command();
    this.commandsRestService.getCount(null).subscribe({
      next: amount => {
        this.commands = Array.from<Command>({ length: amount });
        this.datasource = new MyDataSource(this.commands, this.commandsRestService, null);
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }

  createForm() {
    this.form = this.formBuilder.group({
      callable: true,
      command: '',
      methodName: '',
      room: null,
      owner: null
    });
  }

  resetForm() {
    this.form.reset({
      callable: true,
      command: '',
      methodName: '',
      room: null,
      owner: null
    });
  }

  public cancel(): void {
    this.resetForm();
    this.command = new Command();
  }

  isActive(command: Command) {
    if (command === undefined) {
      return '';
    }
    if (!this.isCommandSelected()) {
      return '';
    }
    return (this.command.id === command.id) ? 'table-active' : '';
  }

  setCommandById(id: string) {
    console.log('setcommandbyid' + id);
    this.commandsRestService.getCommand(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setCommand(data); }
      }
    });
    return false;
  }

  setCommand(command: Command) {
    this.command = command;
    this.form.reset({
      id: command.id,
      callable: command.callable,
      command: command.command,
      methodName: command.methodName,
      room: command.room,
      owner: command.owner
    });
  }

  public deleteCommand(): void {
    if (window.console) {
      console.log('deleteCommand ' + this.command.id);
    }
    this.commandsRestService.deleteCommand(this.command).subscribe(
      (result: any) => { // on success
        this.commands = this.commands.filter((bl) => bl === undefined || bl.id !== this.command.id);
        // this.commands.push(undefined);
        this.commands = [...this.commands];
        this.datasource.updateDatastream(this.commands);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public saveCommand(): void {
    if (window.console) {
      console.log('saveCommand');
    }
    const index = this.commands.indexOf(this.command);
    if (window.console) {
      console.log('saveCommand' + index);
    }
    const command = this.prepareSave();
    this.commandsRestService.updateCommand(command).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('save the command ' + command);
        }
        if (command.id !== undefined) {
          this.commands[index] = command;
        }
        this.commands = [...this.commands];
        this.datasource.updateDatastream(this.commands);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  prepareSave(): Command {
    const formModel = this.form.value;

    // return new `Command` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const saveCommand: Command = {
      id: this.command.id,
      callable: formModel.callable as boolean,
      command: formModel.command as string,
      room: formModel.room as number,
      methodName: formModel.methodName as string,
      creation: this.command.creation as string,
      owner: this.command.owner as string
    };
    return saveCommand;
  }

  disownCommand() {
    if (!this.isCommandSelected()) {
      return;
    }
    this.command.owner = null;
    this.datasource.updateDatastream(this.commands);
  }

  isCommandSelected() {
    return this.command !== undefined && this.command !== null;
  }
}

