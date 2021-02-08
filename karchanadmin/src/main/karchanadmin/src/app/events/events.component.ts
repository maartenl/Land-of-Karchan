import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { EventsRestService } from '../events-rest.service';
import { MudEvent } from './event.model';
import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent extends AdminComponent<MudEvent, number> implements OnInit {
  form: FormGroup;

  constructor(
    private eventsRestService: EventsRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    const object = {
      eventid: null,
      name: null,
      month: null,
      dayofmonth: null,
      hour: null,
      minute: null,
      dayofweek: null,
      callable: null,
      methodname: null,
      room: null,
      owner: null,
      creation: null
    };
    this.form = this.formBuilder.group(object);
    this.makeItem();
    this.getItems();
  }

  ngOnInit() {
  }

  getItems() {
    this.eventsRestService.getAll()
      .subscribe({
        next: data => {
          this.items = data;
        }
      });
  }

  getRestService(): AdminRestService<MudEvent, number> {
    return this.eventsRestService;
  }

  setForm(item?: MudEvent) {
    const object = item === undefined ? {
      eventid: null,
      name: null,
      month: null,
      dayofmonth: null,
      hour: null,
      minute: null,
      dayofweek: null,
      callable: null,
      methodname: null,
      room: null,
      owner: null,
      creation: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): MudEvent {
    return new MudEvent();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): MudEvent {
    const formModel = this.form.value;

    const saveEvent: MudEvent = new MudEvent({
      eventid: formModel.eventid as string,
      name: formModel.name as string,
      month: formModel.month as number,
      dayofmonth: formModel.dayofmonth as number,
      hour: formModel.hour as number,
      minute: formModel.minute as number,
      dayofweek: formModel.dayofweek as number,
      callable: formModel.callable as boolean,
      methodname: formModel.methodname as string,
      room: formModel.room as number,
      owner: formModel.owner as string
    });
    return saveEvent;
  }

  setItemById(id: number | undefined | null) {
    if (id === undefined || id === null) {
      return false;
    }
    this.eventsRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setEvent(data); }
      }
    });
    return false;
  }


  private setEvent(event: MudEvent) {
    this.item = event;
    this.form.reset({
      eventid: event.eventid,
      name: event.name,
      month: event.month,
      dayofmonth: event.dayofmonth,
      hour: event.hour,
      minute: event.minute,
      dayofweek: event.dayofweek,
      callable: event.callable,
      methodname: event.methodname,
      room: event.room,
      owner: event.owner
    });
  }

}
