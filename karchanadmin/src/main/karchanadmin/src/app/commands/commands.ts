import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {ToastService} from '../toast.service';
import {CommandsRestService} from '../commands-rest.service';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {AdminComponent} from '../admin/admin.component';
import {Command} from './command.model';
import {ColDef, SelectionChangedEvent} from "ag-grid-community";
import {Logger} from '../consolelog.service';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';

export interface CommandData {
  command: string;
  methodName: string;
  room: number | null;
  callable: boolean;
  owner: string;
}

@Component({
  selector: 'app-commands',
  imports: [FormField, AgGridAngular, RouterLink
  ],
  templateUrl: './commands.html',
  styleUrl: './commands.css',
})
export class Commands extends AdminComponent<Command, number> implements OnInit {
  override colDefs: ColDef<any, any>[] = [
    {field: "id", filter: true, width: 150},
    {field: "command", filter: true, width: 100},
    {field: "methodName", filter: true, width: 100},
    {field: "room", filter: true, width: 100},
    {field: "owner", filter: true, width: 100},
    {field: "creation"}
  ];

  override restService = inject(CommandsRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  commandModel = signal<CommandData>({
    callable: false,
    command: "",
    methodName: "",
    owner: "",
    room: null
  });

  form = form(this.commandModel);

  ngOnInit(): void {
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

  override setForm(): void {
    const command = this.item();
    this.commandModel.set({
      command: command.command ?? "",
      callable: command.callable ?? false,
      methodName: command.methodName ?? "",
      owner: command.owner ?? "",
      room: command.room ?? null,
    });
  }

  override makeItem(): Command {
    return new Command();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].id);
  }

  override getForm(): Command {
    const command = this.item();
    const formModel = this.commandModel();
    command.command = formModel.command;
    command.callable = formModel.callable;
    command.methodName = formModel.methodName;
    command.owner = formModel.owner == "" ? null : formModel.owner;
    command.room = formModel.room;
    return command;
  }

  override setItemById(id: number | null | undefined): boolean {
    if (id === undefined || id === null) {
      return false;
    }
    this.restService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.item.set(data);
          this.setForm();
        }
      }
    });
    return false;
  }

  protected readonly rowSelection = rowSelection;
}
