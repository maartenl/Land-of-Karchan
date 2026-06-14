import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {AdminComponent} from '../admin/admin.component';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {ToastService} from '../toast.service';
import {RoomsRestService} from '../rooms-rest.service';
import {ItemSub} from '../item-sub/item-sub';
import {AttributesSub} from '../attributes-sub/attributes-sub';
import {AgGridAngular} from 'ag-grid-angular';
import {form, FormField} from '@angular/forms/signals';
import {Room} from './room.model';
import {rowSelection} from '../aggrid.utils';
import {ColDef, GridReadyEvent, SelectionChangedEvent} from 'ag-grid-community';
import {Logger} from '../consolelog.service';
import {Command} from '../commands/command.model';
import {AdminMudType} from '../adminmudtype';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {AdminRestService} from '../admin/admin-rest.service';
import {MudCharacter} from '../characters/character.model';

export interface RoomData {
  id: number | null;
  title: string;
  picture: string;
  contents: string;
  north: number | null;
  south: number | null;
  west: number | null;
  east: number | null;
  up: number | null;
  down: number | null;
  owner: string;
  area: string;
}

@Component({
  selector: 'app-rooms',
  imports: [AgGridAngular, FormField, ItemSub, AttributesSub],
  templateUrl: './rooms.html',
  styleUrl: './rooms.css',
})
export class Rooms extends AdminComponent<Room, number> implements OnInit {
  override restService = inject(RoomsRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  itemSub = viewChild.required(ItemSub);
  attributesSub = viewChild.required(AttributesSub);

  // Column Definitions: Defines & controls grid columns.
  override colDefs: ColDef[] = [
    {field: "id", filter: true, width: 150},
    {field: "title", filter: true, width: 100},
    {field: "area", filter: true, width: 100},
    {field: "owner", filter: true, width: 100},
  ];

  // Column Definitions: Defines & controls grid columns.
  colDefsCommands: ColDef[] = [
    {field: "id", filter: true, width: 150, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/commands"}},
    {field: "command", filter: true, width: 100},
  ];

  // Column Definitions: Defines & controls grid columns.
  colDefsCharacters: ColDef[] = [
    {field: "name", filter: true, width: 150, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/characters"}},
    {field: "room", filter: true, width: 100},
    {field: "race", width: 100},
    {field: "sex", width: 100},
    {field: "address", filter: true},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  commands = signal<Command[]>([]);
  characters = signal<MudCharacter[]>([]);

  roomModel = signal<RoomData>({
    id: null,
    title: "",
    picture: "",
    contents: "",
    north: null,
    south: null,
    west: null,
    east: null,
    up: null,
    down: null,
    owner: "",
    area: "",
  })

  form = form(this.roomModel);

  ngOnInit() {
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

  override setItemById(id: number | undefined | null) {
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
    this.restService.getCommands(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.commands.set(data);
        }
      }
    });
    this.restService.getCharacters(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.characters.set(data);
        }
      }
    });
    this.itemSub().getItemsOfRoom(id);
    this.attributesSub().getAttributesOfRoom(id);
    return false;
  }

  onSelectionChanged = (event: SelectionChangedEvent) => {
    this.setItemById(event.api.getSelectedRows()[0].id);
  };

  override makeItem(): Room {
    return new Room();
  }

  override setForm() {
    const room = this.item();
    this.roomModel.set({
      id: room.id ?? null,
      title: room.title ?? "",
      picture: room.picture ?? "",
      contents: room.contents ?? "",
      north: room.north ?? null,
      south: room.south ?? null,
      west: room.west ?? null,
      east: room.east ?? null,
      up: room.up ?? null,
      down: room.down ?? null,
      owner: room.owner ?? "",
      area: room.area ?? "",
    })
  }

  override getForm(): Room {
    const room = this.item();
    const formModel = this.roomModel();
    room.area = formModel.area == "" ? null : formModel.area;
    room.contents = formModel.contents == "" ? null : formModel.contents;
    room.down = formModel.down;
    room.east = formModel.east;
    room.id = formModel.id;
    room.north = formModel.north;
    room.owner = formModel.owner == "" ? null : formModel.owner;
    room.picture = formModel.picture == "" ? null : formModel.picture;
    room.south = formModel.south;
    room.title = formModel.title == "" ? null : formModel.title;
    room.up = formModel.up;
    room.west = formModel.west;
    return room;
  }

  protected readonly rowSelection = rowSelection;
  protected readonly AdminMudType = AdminMudType;
}
