import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { CommandsRestService } from '../commands-rest.service';
import { Command } from './command.model';

@Component({
  selector: 'app-commands',
  templateUrl: './commands.component.html',
  styleUrls: ['./commands.component.css']
})
export class CommandsComponent implements OnInit {

  commands: Command[];

  command: Command;

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
    this.commandsRestService.getCommands().subscribe({
      next: data => {
        this.commands = data;
      }
    });
  }

  constructor(
    private commandsRestService: CommandsRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder) {
    this.createForm();
    this.command = new Command();
    this.commandsRestService.getCommands().subscribe({
      next: data => {
        this.commands = data;
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
    const id: string = this.route.snapshot.paramMap.get('id');
    if (id === undefined || id === null) {
      return;
    }
    this.setCommandById(id);
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

  private setCommand(command: Command) {
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
        this.commands = [...this.commands];
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public createCommand(): void {
    if (window.console) {
      console.log('createCommand');
    }
    const command = this.prepareSave();
    this.commandsRestService.createCommand(command).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('save the command ' + command);
        }
        this.commands.push(command);
        this.commands = [...this.commands];
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public updateCommand(): void {
    if (window.console) {
      console.log('updateCommand');
    }
    const command = this.prepareSave();
    const index = this.commands.findIndex(cmd => cmd != null && cmd.id === command.id);
    if (window.console) {
      console.log('saveCommand' + index);
    }
    this.commandsRestService.updateCommand(command).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('save the command ' + command);
        }
        if (command.id !== undefined) {
          this.commands[index] = command;
        }
        this.commands = [...this.commands];
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

  isCommandSelected() {
    return this.command !== undefined && this.command !== null;
  }

  refresh() {
    this.commandsRestService.clearCache();
    this.commandsRestService.getCommands().subscribe({
      next: data => {
        this.commands = data;
      }
    });
  }
}

