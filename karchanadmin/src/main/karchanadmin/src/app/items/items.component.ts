import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { ItemsRestService } from '../items-rest.service';
import { Item } from './item.model';
import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-items',
  templateUrl: './items.component.html',
  styleUrls: ['./items.component.css']
})
export class ItemsComponent extends AdminComponent<Item, number> implements OnInit {

  form: FormGroup;

  SearchTerms = class {
    owner: string;
    name: string;
  };

  searchTerms = new this.SearchTerms();

  updateOwner(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.owner = value;
    this.getItems();
  }

  updateName(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.name = value;
    this.getItems();
  }

  constructor(
    private itemsRestService: ItemsRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    this.setForm();
    this.makeItem();
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

  getItems() {
    this.itemsRestService.getAll()
      .subscribe({
        next: data => {
          const ownerFilter = method => this.searchTerms.owner === undefined ||
            this.searchTerms.owner === null ||
            this.searchTerms.owner === method.owner;
          const nameFilter = character => this.searchTerms.name === undefined ||
            this.searchTerms.name === null ||
            character.name.includes(this.searchTerms.name);
          this.items = data.filter(ownerFilter).filter(nameFilter);
        }
      });
  }

  setForm(item?: Item) {
    const object = item === undefined ? {
      id: null,
      adject1: '',
      adject2: '',
      adject3: '',
      name: '',
      room: 0,
      manaincrease: 0,
      hitincrease: 0,
      vitalincrease: 0,
      movementincrease: 0,
      pasdefense: 0,
      damageresistance: 0,
      eatable: '',
      drinkable: '',
      lightable: false,
      getable: true,
      dropable: true,
      visible: true,
      wieldable: 0,
      description: '',
      readdescr: '',
      wearable: true,
      copper: 0,
      weight: 0,
      container: 0,
      capacity: 0,
      isopenable: false,
      keyid: null,
      containtype: 0,
      notes: '',
      image: '',
      title: '',
      discriminator: 0,
      bound: false,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  setItemById(id: number) {
    this.itemsRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setItem(data); }
      }
    });
    return false;
  }

  private setItem(item: Item) {
    this.item = item;
    this.form.reset({
      id: item.id,
      adject1: item.adject1,
      adject2: item.adject2,
      adject3: item.adject3,
      name: item.name,
      room: item.room,
      manaincrease: item.manaincrease,
      hitincrease: item.hitincrease,
      vitalincrease: item.vitalincrease,
      movementincrease: item.movementincrease,
      pasdefense: item.pasdefense,
      damageresistance: item.damageresistance,
      eatable: item.eatable,
      drinkable: item.drinkable,
      lightable: item.lightable,
      getable: item.getable,
      dropable: item.dropable,
      visible: item.visible,
      wieldable: item.wieldable,
      description: item.description,
      readdescr: item.readdescr,
      wearable: item.wearable,
      copper: item.copper,
      weight: item.weight,
      container: item.container,
      capacity: item.capacity,
      isopenable: item.isopenable,
      keyid: item.keyid,
      containtype: item.containtype,
      notes: item.notes,
      image: item.image,
      title: item.title,
      discriminator: item.discriminator,
      bound: item.bound,
      owner: item.owner,
      creation: item.creation
    });
  }

  getForm(): Item {
    const formModel = this.form.value;

    // return new `Item` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const id = this.item === undefined ? null : this.item.id;
    const creation = this.item === undefined ? null : this.item.creation;
    const owner = this.item === undefined ? null : this.item.owner;
    const saveItem: Item = new Item({
      id,
      adject1: formModel.adject1 as string,
      adject2: formModel.adject2 as string,
      adject3: formModel.adject3 as string,
      name: formModel.name as string,
      room: formModel.room as number,
      eatable: formModel.eatable as string,
      drinkable: formModel.drinkable as string,
      lightable: formModel.lightable as boolean,
      getable: formModel.getable as boolean,
      dropable: formModel.dropable as boolean,
      visible: formModel.visible as boolean,
      wieldable: formModel.wieldable as boolean,
      description: formModel.description as string,
      readdescr: formModel.readdescr as string,
      wearable: formModel.wearable as number,
      copper: formModel.copper as number,
      weight: formModel.weight as number,
      container: formModel.container as number,
      capacity: formModel.capacity as number,
      isopenable: formModel.isopenable as boolean,
      keyid: formModel.keyid as number,
      containtype: formModel.containtype as number,
      notes: formModel.notes as string,
      image: formModel.image as string,
      title: formModel.title as string,
      discriminator: formModel.discriminator as number,
      bound: formModel.bound as boolean,
      creation,
      owner
    });
    console.log(saveItem);
    return saveItem;
  }

  getRestService(): ItemsRestService {
    return this.itemsRestService;
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  makeItem(): Item {
    return new Item();
  }

  sortById() {
    if (window.console) {
      console.log('sortById');
    }
    this.items = this.items.sort((a, b) => a.id - b.id);
    this.items = [...this.items];
    return false;
  }

  sortByAdject1() {
    if (window.console) {
      console.log('sortByAdject1');
    }
    this.items = this.items.sort((a, b) => { 
      if (a.adject1 === b.adject1) {
        return 0;
      }
      else if (a.adject1 === null) {
        return 1;
      } 
      else if (b.adject1 === null) {
        return -1;
      }
      return a.adject1.localeCompare(b.adject1) 
    });
    this.items = [...this.items];
    return false;
  }

  sortByAdject2() {
    if (window.console) {
      console.log('sortByAdject2');
    }
    this.items = this.items.sort((a, b) => { 
      if (a.adject2 === b.adject2) {
        return 0;
      }
      else if (a.adject2 === null) {
        return 1;
      } 
      else if (b.adject2 === null) {
        return -1;
      }
      return a.adject2.localeCompare(b.adject2) 
    });
    this.items = [...this.items];
    return false;
  }

  sortByAdject3() {
    if (window.console) {
      console.log('sortByAdject3');
    }
    this.items = this.items.sort((a, b) => { 
      if (a.adject3 === b.adject3) {
        return 0;
      }
      else if (a.adject3 === null) {
        return 1;
      } 
      else if (b.adject3 === null) {
        return -1;
      }
      return a.adject3.localeCompare(b.adject3) 
    });
    this.items = [...this.items];
    return false;
  }

  sortByName() {
    if (window.console) {
      console.log('sortByName');
    }
    this.items = this.items.sort((a, b) => a.name.localeCompare(b.name));
    this.items = [...this.items];
    return false;
  }

}
