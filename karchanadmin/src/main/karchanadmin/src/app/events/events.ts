import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {MudEvent} from './event.model';
import {AdminComponent} from '../admin/admin.component';
import {ToastService} from '../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {EventsRestService} from '../events-rest.service';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';

export interface EventData {
  eventid: number | null;
  name: string;
  month: number | null;
  dayofmonth: number | null;
  hour: number | null;
  minute: number | null;
  dayofweek: number | null;
  callable: boolean;
  methodname: string;
  room: number | null;
  owner: string;
}

@Component({
  selector: 'app-events',
  imports: [FormField, AgGridAngular
  ],
  templateUrl: './events.html',
  styleUrl: './events.css',
})
export class Events extends AdminComponent<MudEvent, number> {

  override restService = inject(EventsRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "eventid", filter: true, width: 150},
    {field: "name", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/characters"}},
    {
      field: "methodname",
      filter: true,
      width: 100,
      cellRenderer: LinkRenderer,
      cellRendererParams: {prefix: "/methods"}
    },
    {field: "room", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/rooms"}},
    {field: "owner", filter: true, width: 100},
    {field: "creation", filter: true, width: 100},
  ];

  eventModel = signal<EventData>({
    callable: false,
    dayofmonth: null,
    dayofweek: null,
    eventid: null,
    hour: null,
    methodname: "",
    minute: null,
    month: null,
    name: "",
    owner: "",
    room: null
  });

  form = form(this.eventModel);

  override setForm(): void {
    const event = this.item();
    this.eventModel.set({
      eventid: event.eventid ?? null,
      callable: event.callable ?? false,
      dayofmonth: event.dayofmonth ?? null,
      dayofweek: event.dayofweek ?? null,
      hour: event.hour ?? null,
      methodname: event.methodname ?? "",
      minute: event.minute ?? null,
      month: event.month ?? null,
      name: event.name ?? "",
      owner: event.owner ?? "",
      room: event.room ?? null,
    });
  }

  override makeItem(): MudEvent {
    return new MudEvent();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].eventid);
  }

  override getForm(): MudEvent {
    const method = this.item();
    const formModel = this.eventModel();
    method.eventid = formModel.eventid;
    method.name = formModel.name == "" ? null : formModel.name;
    method.month = formModel.month;
    method.dayofmonth = formModel.dayofmonth;
    method.hour = formModel.hour;
    method.minute = formModel.minute;
    method.dayofweek = formModel.dayofweek;
    method.callable = formModel.callable;
    method.methodname = formModel.methodname == "" ? null : formModel.methodname;
    method.room = formModel.room;
    method.owner = formModel.owner == "" ? null : formModel.owner;
    return method;
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
