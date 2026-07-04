import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {MudCharacter, Sex} from './character.model';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {ToastService} from '../toast.service';
import {CharactersRestService} from '../characters-rest.service';

import {AgGridAngular} from "ag-grid-angular";
import {ColDef, GridReadyEvent, SelectionChangedEvent, RowSelectionOptions,} from "ag-grid-community";
import {Logger} from '../consolelog.service';
import {form, FormField} from '@angular/forms/signals';
import {ItemSub} from '../item-sub/item-sub';
import {AttributesSub} from '../attributes-sub/attributes-sub';
import {AdminComponent} from '../admin/admin.component';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {AdminMudType} from '../adminmudtype';
import {rowSelection} from '../aggrid.utils';

export interface MudCharacterData {
  name: string;
  image: string;
  familyname: string;
  title: string;
  room: number | null;
  active: boolean;
  god: string;
  race: string;
  sex: string;
  age: string;
  height: string;
  width: string;
  complexion: string;
  eyes: string;
  face: string;
  hair: string;
  beard: string;
  arm: string;
  leg: string;
  afk: string;
  birth: Date;
  lastlogin: Date;
  address: string;
  realname: string;
  newpassword: string;
  state: string;
  storyline: string;
  notes: string;
  owner: string;
}

@Component({
  selector: 'app-characters',
  imports: [AgGridAngular, FormField, RouterLink, ItemSub, AttributesSub],
  templateUrl: './characters.html',
  styleUrl: './characters.css',
})
export class Characters extends AdminComponent<MudCharacter, string> implements OnInit {

  override restService = inject(CharactersRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  itemSub = viewChild.required(ItemSub);
  attributesSub = viewChild.required(AttributesSub);

  characterModel = signal<MudCharacterData>({
    active: false,
    address: "",
    age: "",
    arm: "",
    beard: "",
    birth: new Date(),
    complexion: "",
    eyes: "",
    face: "",
    familyname: "",
    god: "",
    hair: "",
    height: "",
    image: "",
    lastlogin: new Date(),
    leg: "",
    afk: "",
    name: "",
    newpassword: "",
    notes: "",
    owner: "",
    race: "",
    realname: "",
    room: null,
    sex: "undefined",
    title: "",
    width: "",
    state: "",
    storyline: "",
  });

  form = form(this.characterModel)

  // Column Definitions: Defines & controls grid columns.
  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 150},
    {field: "room", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/rooms"}},
    {field: "race", width: 100},
    {field: "sex", width: 100},
    {field: "address", filter: true},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  ngOnInit() {
    const name: string | null = this.route.snapshot.paramMap.get('name');
    if (name === undefined || name === null) {
      Logger.logEntering("ngOnInit");
      return;
    }
    Logger.logEntering("ngOnInit " + name);
    this.setItemById(name);
  }

  setItemById(name: string | undefined | null) {
    if (name === undefined || name === null) {
      return false;
    }
    this.restService.get(name).subscribe({
      next: (data: MudCharacter) => {
        if (data !== undefined) {
          this.item.set(data);
          this.setForm();
        }
      }
    });
    this.itemSub().getItemsOfCharacter(name);
    this.attributesSub().getAttributesOfCharacter(name);
    return false;
  }

  onSelectionChanged = (event: SelectionChangedEvent) => {
    this.setItemById(event.api.getSelectedRows()[0].name);
  };

  override setForm() {
    const character = this.item();
    this.characterModel.set({
      active: character.active ?? false,
      address: character.address ?? "",
      age: character.age ?? "",
      arm: character.arm ?? "",
      beard: character.beard ?? "",
      birth: character.birth ?? new Date(),
      complexion: character.complexion ?? "",
      eyes: character.eyes ?? "",
      face: character.face ?? "",
      familyname: character.familyname ?? "",
      god: "DEFAULT_USER",
      hair: character.hair ?? "",
      height: character.height ?? "",
      image: character.image ?? "",
      lastlogin: character.lastlogin ?? new Date(),
      leg: character.leg ?? "",
      afk: character.afk ?? "",
      name: character.name ?? "",
      newpassword: character.newpassword ?? "",
      notes: character.notes ?? "",
      owner: character.owner ?? "",
      race: character.race ?? "",
      realname: character.realname ?? "",
      room: character.room ?? null,
      sex: character.sex ?? "",
      title: character.title ?? "",
      width: character.width ?? "",
      state: character.state ?? "",
      storyline: character.storyline ?? "",
    });
  }

  override makeItem(): MudCharacter {
    return new MudCharacter();
  }

  override getForm(): MudCharacter {
    const character = this.item();
    const formModel = this.characterModel();
    character.active = formModel.active;
    character.address = formModel.address;
    character.age = formModel.age == "" ? null : formModel.age;
    character.arm = formModel.arm == "" ? null : formModel.arm;
    character.beard = formModel.beard == "" ? null : formModel.beard;
    character.birth = formModel.birth;
    character.complexion = formModel.complexion == "" ? null : formModel.complexion;
    character.eyes = formModel.eyes == "" ? null : formModel.eyes;
    character.face = formModel.face == "" ? null : formModel.face;
    character.familyname = formModel.familyname == "" ? null : formModel.familyname;
    // character.god = "DEFAULT_USER";
    character.hair = formModel.hair == "" ? null : formModel.hair;
    character.height = formModel.height == "" ? null : formModel.height;
    character.image = formModel.image == "" ? null : formModel.image;
    character.lastlogin = formModel.lastlogin;
    character.leg = formModel.leg == "" ? null : formModel.leg;
    character.afk = formModel.afk == "" ? null : formModel.afk;
    character.name = formModel.name == "" ? null : formModel.name;
    character.newpassword = formModel.newpassword == "" ? null : formModel.newpassword;
    character.notes = formModel.notes == "" ? null : formModel.notes;
    character.owner = formModel.owner == "" ? null : formModel.owner;
    character.race = formModel.race == "" ? null : formModel.race;
    character.realname = formModel.realname == "" ? null : formModel.realname;
    character.room = formModel.room;
    switch (formModel.sex) {
      case "male": {
        character.sex = Sex.Male;
        break;
      }
      case "female": {
        character.sex = Sex.Female;
        break;
      }
      case "other": {
        character.sex = Sex.Other;
        break;
      }
      default: {
        this.toastService.showError("Sex should be 'male','female' or 'other'.", "Invalid sex.");
        throw new TypeError("Sex should be 'male','female' or 'other'.")
      }
    }
    character.title = formModel.title;
    character.width = formModel.width;
    character.state = formModel.state == "" ? null : formModel.state;
    character.storyline = formModel.storyline == "" ? null : formModel.storyline;
    return character;
  }

  protected readonly rowSelection = rowSelection;
  protected readonly AdminMudType = AdminMudType;
}
