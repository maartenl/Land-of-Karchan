import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {ItemDefinition} from './item.model';
import {AdminComponent} from '../admin/admin.component';
import {ActivatedRoute} from '@angular/router';
import {ToastService} from '../toast.service';
import {ItemdefinitionsRestService} from '../itemdefinitions-rest.service';
import {form, FormField} from '@angular/forms/signals';
import {ColDef, SelectionChangedEvent} from "ag-grid-community";
import {ItemSub} from '../item-sub/item-sub';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';
import {AdminMudType} from '../adminmudtype';
import {Logger} from '../consolelog.service';

interface ItemdefinitionData {
  id: number | null;
  adjectives: string;
  name: string;
  room: number | null;
  manaincrease: number | null;
  hitincrease: number | null;
  vitalincrease: number | null;
  movementincrease: number | null;
  pasdefense: number | null;
  damageresistance: number | null;
  eatable: string;
  drinkable: string;
  lightable: boolean;
  getable: boolean;
  dropable: boolean;
  visible: boolean;
  wieldable: string;
  description: string;
  readdescr: string;
  wearable: string;
  copper: number | null;
  weight: number | null;
  container: boolean;
  capacity: number | null;
  isopenable: boolean;
  keyid: number | null;
  containtype: number | null;
  notes: string;
  image: string;
  title: string;
  discriminator: number | null;
  bound: boolean;
  owner: string;
}

@Component({
  selector: 'app-items',
  imports: [AgGridAngular, FormField, ItemSub],
  templateUrl: './items.html',
  styleUrl: './items.css',
})
export class Items extends AdminComponent<ItemDefinition, number> implements OnInit {

  override restService = inject(ItemdefinitionsRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  itemSub = viewChild.required(ItemSub);

  override colDefs: ColDef[] = [
    {field: "id", filter: true, width: 150},
    {field: "adjectives", filter: true, width: 100},
    {field: "name", filter: true, width: 100},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  itemdefinitionModel = signal<ItemdefinitionData>({
    id: null,
    adjectives: '',
    name: '',
    room: null,
    manaincrease: null,
    hitincrease: null,
    vitalincrease: null,
    movementincrease: null,
    pasdefense: null,
    damageresistance: null,
    eatable: '',
    drinkable: '',
    lightable: false,
    getable: true,
    dropable: true,
    visible: true,
    wieldable: '',
    description: '',
    readdescr: '',
    wearable: '',
    copper: null,
    weight: null,
    container: false,
    capacity: null,
    isopenable: false,
    keyid: null,
    containtype: null,
    notes: '',
    image: '',
    title: '',
    discriminator: null,
    bound: false,
    owner: ''
  });

  form = form(this.itemdefinitionModel)

  ngOnInit() {
    const id: string | null = this.route.snapshot.paramMap.get('id');
    if (id === null) {
      return;
    }
    const idNumber: number = Number(id);
    if (isNaN(idNumber)) {
      return;
    }
    this.route.params.subscribe(res => {
      if (res['id'] !== 0) this.setItemById(res['id']) // 'id' here is an example url parameter
    })
    this.setItemById(idNumber);
  }

  setItemById(id: number | undefined | null) {
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
    this.itemSub().getItemsOfItemdefinition(id);
    return false;
  }

  override setForm(): void {
    const itemDefinition = this.item();
    Logger.logEntering("setForm");
    this.itemdefinitionModel.set({
      id: itemDefinition.id,
      adjectives: itemDefinition.adjectives ?? "",
      name: itemDefinition.name ?? "",
      room: itemDefinition.room == undefined ? null : itemDefinition.room,
      manaincrease: itemDefinition.manaincrease,
      hitincrease: itemDefinition.hitincrease,
      vitalincrease: itemDefinition.vitalincrease,
      movementincrease: itemDefinition.movementincrease,
      pasdefense: itemDefinition.pasdefense,
      damageresistance: itemDefinition.damageresistance,
      eatable: itemDefinition.eatable ?? "",
      drinkable: itemDefinition.drinkable ?? "",
      lightable: itemDefinition.lightable ?? false,
      getable: itemDefinition.getable ?? false,
      dropable: itemDefinition.dropable ?? false,
      visible: itemDefinition.visible ?? false,
      wieldable: itemDefinition.wieldable ?? "",
      description: itemDefinition.description ?? "",
      readdescr: itemDefinition.readdescr ?? "",
      wearable: itemDefinition.wearable ?? "",
      copper: itemDefinition.copper,
      weight: itemDefinition.weight,
      container: itemDefinition.container ?? false,
      capacity: itemDefinition.capacity == undefined ? null : itemDefinition.capacity,
      isopenable: itemDefinition.isopenable ?? false,
      keyid: itemDefinition.keyid == undefined ? null : itemDefinition.keyid,
      containtype: itemDefinition.containtype == undefined ? null : itemDefinition.containtype,
      notes: itemDefinition.notes ?? "",
      image: itemDefinition.image ?? "",
      title: itemDefinition.title ?? "",
      discriminator: itemDefinition.discriminator,
      bound: itemDefinition.bound ?? false,
      owner: itemDefinition.owner ?? "",
    })
    Logger.logObject(this.itemdefinitionModel());
  }

  override getForm(): ItemDefinition {
    const itemDefinition = this.item();
    const formModel = this.itemdefinitionModel();
    itemDefinition.id = formModel.id;
    itemDefinition.adjectives = formModel.adjectives;
    itemDefinition.name = formModel.name;
    itemDefinition.room = formModel.room;
    itemDefinition.manaincrease = formModel.manaincrease;
    itemDefinition.hitincrease = formModel.hitincrease;
    itemDefinition.vitalincrease = formModel.vitalincrease;
    itemDefinition.movementincrease = formModel.movementincrease;
    itemDefinition.pasdefense = formModel.pasdefense;
    itemDefinition.damageresistance = formModel.damageresistance;
    itemDefinition.eatable = formModel.eatable == "" ? null : formModel.eatable;
    itemDefinition.drinkable = formModel.drinkable == "" ? null : formModel.drinkable;
    itemDefinition.lightable = formModel.lightable;
    itemDefinition.getable = formModel.getable;
    itemDefinition.dropable = formModel.dropable;
    itemDefinition.visible = formModel.visible;
    itemDefinition.wieldable = formModel.wieldable == "" ? null : formModel.wieldable;
    itemDefinition.description = formModel.description == "" ? null : formModel.description;
    itemDefinition.readdescr = formModel.readdescr == "" ? null : formModel.readdescr;
    itemDefinition.wearable = formModel.wearable == "" ? null : formModel.wearable;
    itemDefinition.copper = formModel.copper;
    itemDefinition.weight = formModel.weight;
    itemDefinition.container = formModel.container;
    itemDefinition.capacity = formModel.capacity;
    itemDefinition.isopenable = formModel.isopenable;
    itemDefinition.keyid = formModel.keyid;
    itemDefinition.containtype = formModel.containtype;
    itemDefinition.notes = formModel.notes == "" ? null : formModel.notes;
    itemDefinition.image = formModel.image == "" ? null : formModel.image;
    itemDefinition.title = formModel.title == "" ? null : formModel.title;
    itemDefinition.discriminator = formModel.discriminator;
    itemDefinition.bound = formModel.bound;
    itemDefinition.owner = formModel.owner == "" ? null : formModel.owner;
    return itemDefinition;
  }

  override makeItem(): ItemDefinition {
    return new ItemDefinition();
  }

  override onSelectionChanged = (event: SelectionChangedEvent) => {
    this.setItemById(event.api.getSelectedRows()[0].id);
  };

  protected readonly rowSelection = rowSelection;
  protected readonly AdminMudType = AdminMudType;
}
