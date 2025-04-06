import {ActivatedRoute} from '@angular/router';
import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {AdminComponent} from '../admin/admin.component'
import {RoomsRestService} from '../rooms-rest.service';
import {Room} from './room.model';
import {Command} from '../commands/command.model';
import {ToastService} from '../toast.service';
import {Item} from "../items/item.model";
import { Attribute } from '../attribute.model';
import { AttributesRestService } from '../attributes-rest.service';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent extends AdminComponent<Room, number> implements OnInit {
  iteminstances: Item[] = [] = new Array<Item>(0);

  commands: Command[] = [] = new Array<Command>(0);

  form: FormGroup;

  attributeForm: FormGroup;
  
  attributes: Array<Attribute> = [];
  
  attribute: Attribute | null = null;

  SearchTerms = class {
    owner: string | null = null;
    title: string | null = null;
    contents: string | null = null;
    area: string | null = null;
  };

  searchTerms = new this.SearchTerms();

  makeItem(): Room {
    return new Room();
  }

  get room(): Room | null {
    return this.item;
  }

  updateOwnerSearch(value: string) {
    this.searchTerms.owner = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateTitleSearch(value: string) {
    this.searchTerms.title = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateDescriptionSearch(value: string) {
    this.searchTerms.contents = value.trim() === '' ? null : value;
    this.getItems();
  }

  updateAreaSearch(value: string) {
    this.searchTerms.area = value.trim() === '' ? null : value;
    this.getItems();
  }

  constructor(
    private roomsRestService: RoomsRestService,
    private attributesRestService: AttributesRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    const object = {
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
    };
    this.form = this.formBuilder.group(object);
    const attribute = {
      id: null,
      name: null,
      value: null,
      valueType: null
    }
    this.attributeForm = this.formBuilder.group(attribute);
    this.item = this.makeItem();
    this.getItems();
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
    } else {
      this.form.reset(object);
    }
  }

  getRestService(): RoomsRestService {
    return this.roomsRestService;
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  setItemById(id: number | undefined | null) {
    if (id === undefined || id === null) {
      return false;
    }
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
    this.roomsRestService.getAllItems(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.iteminstances = data;
        }
      }
    });
    this.attributesRestService.getFromRoom(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.attributes = data;
        }
      }
    })
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
      creation: null,
      owner: formModel.owner as string
    });
    return saveRoom;
  }

  getItems() {
    this.roomsRestService.getAll(this.searchTerms.contents).subscribe({
      next: data => {
        const ownerFilter = (room: Room) => this.searchTerms.owner === undefined ||
          this.searchTerms.owner === null ||
          this.searchTerms.owner === room.owner;
        const titleFilter = (room: Room) => this.searchTerms.title === undefined ||
          this.searchTerms.title === null ||
          (room.title !== null && room.title.includes(this.searchTerms.title));
        const areaFilter = (room: Room) => this.searchTerms.area === undefined ||
          this.searchTerms.area === null ||
          room.area === this.searchTerms.area;
        this.items = data.filter(ownerFilter).filter(titleFilter).filter(areaFilter);
      }
    });
  }

  setAttribute(attribute: Attribute) {
    this.attribute = attribute;
    this.setAttributeForm(attribute);
    return false;
  }

  deleteAttribute(attribute: Attribute) {
    if (window.console) {
      console.log("deleteAttribute " + attribute);
    }
    this.attributesRestService.delete(attribute).subscribe(
      (result: any) => { // on success
        const index = this.attributes.indexOf(attribute, 0);
        if (index > -1) {
          this.attributes.splice(index, 1);
        }
        this.toastService.show('Attribute successfully deleted.', {
          delay: 3000,
          autohide: true,
          headertext: 'Deleted...'
        });
      }
    );
    return false;
  }

  updateAttribute() {
    const attribute = this.getAttributeForm();
    if (window.console) {
      console.log("updateAttribute " + attribute);
    }
    if (attribute === null || attribute === undefined) {
      return false;
    }
    this.attributesRestService.update(attribute).subscribe(
      (result: any) => { // on success
        this.attributes = this.attributes.map(u => u.id !== attribute.id ? u : attribute);
        this.toastService.show('Attribute successfully updated.', {
          delay: 3000,
          autohide: true,
          headertext: 'Updated...'
        });
      }
    );
    return false;
  }

  createAttribute() {
    const attribute = this.getAttributeForm();
    if (window.console) {
      console.log("createAttribute " + attribute);
    }
    if (attribute === null || attribute === undefined) {
      return false;
    }
    attribute.id = null;
    this.attributesRestService.update(attribute).subscribe(
      (result: any) => { // on success
        this.attributes.push(attribute);
        this.toastService.show('Attribute successfully created.', {
          delay: 3000,
          autohide: true,
          headertext: 'Created...'
        });
      }
    );
    return false;
  }

  cancelAttribute() {
    this.setAttributeForm();
    return false;
  }

  setAttributeForm(item?: Attribute) {
    const object = item === undefined ? {
      id: null,
      name: null,
      value: null,
      valueType: null,
    } : item;
    if (this.attributeForm === undefined) {
      this.attributeForm = this.formBuilder.group(object);
    } else {
      this.attributeForm.reset(object);
    }
  }

  getAttributeForm(): Attribute | null {
    const formModel = this.attributeForm.value;

    if (this.item === null || this.item === undefined) {
      return null;
    }

    const saveAttribute: Attribute = new Attribute({
      id: formModel.id as number,
      name: formModel.name as string,
      value: formModel.value as string,
      valueType: formModel.valueType as string,
      objecttype: 'ROOM',
      objectid: this.item.id as number,
    });
    return saveAttribute;
  }

}
