import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { AdminComponent } from '../admin/admin.component'
import { RoomsRestService } from '../rooms-rest.service';
import { Room } from './room.model';
import { Command } from '../commands/command.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent extends AdminComponent<Room, number> implements OnInit {

  commands: Command[] = [];

  form: FormGroup;

  SearchTerms = class {
    owner: string;
    title: string;
    contents: string;
    area: string;
  };

  searchTerms = new this.SearchTerms();

  makeItem(): Room {
    return new Room();
  }

  get room(): Room {
    return this.item;
  }

  updateOwnerSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.owner = value;
    this.getItems();
  }

  updateTitleSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.title = value;
    this.getItems();
  }

  updateDescriptionSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.contents = value;
    this.getItems();
  }

  updateAreaSearch(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.area = value;
    this.getItems();
  }

  constructor(
    private roomsRestService: RoomsRestService,
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
    const id: string = this.route.snapshot.paramMap.get('id');
    if (id === undefined || id === null) {
      return;
    }
    const idNumber: number = Number(id);
    if (isNaN(idNumber)) {
      return;
    }
    this.setItemById(idNumber);
  }

  setForm(item?: Room) {
    const object = item === undefined ? {
      id: null,
      title: '',
      picture: null,
      contents: null,
      north: null,
      south: null,
      west: null,
      east: null,
      up: null,
      down: null,
      owner: null,
      area: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else { this.form.reset(object); }
  }

  getRestService(): RoomsRestService {
    return this.roomsRestService;
  }

  setItemById(id: number) {
    console.log('setitembyid' + id);
    this.roomsRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.item = data;
          this.setForm(data);
        }
      }
    });
    this.roomsRestService.getCommands(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.commands = data; }
      }
    });
    return false;
  }


  getForm(): Room {
    const formModel = this.form.value;

    // return new `Room` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const saveRoom: Room = new Room({
      id: formModel.id as number,
      title: formModel.title as string,
      contents: formModel.contents as string,
      area: formModel.area as string,
      picture: formModel.picture as string,
      north: formModel.north as number,
      south: formModel.south as number,
      west: formModel.west as number,
      east: formModel.east as number,
      up: formModel.up as number,
      down: formModel.down as number,
      creation: this.item.creation as string,
      owner: formModel.owner as string
    });
    return saveRoom;
  }

  getItems() {
    this.roomsRestService.getAll(this.searchTerms.contents).subscribe({
      next: data => {
        const ownerFilter = room => this.searchTerms.owner === undefined ||
          this.searchTerms.owner === null ||
          this.searchTerms.owner === room.owner;
        const titleFilter = room => this.searchTerms.title === undefined ||
          this.searchTerms.title === null ||
          room.title.includes(this.searchTerms.title);
        const areaFilter = room => this.searchTerms.area === undefined ||
          this.searchTerms.area === null ||
          room.area === this.searchTerms.area;
        this.items = data.filter(ownerFilter).filter(titleFilter).filter(areaFilter);
      }
    });
  }
}
