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
          this.items = data;
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
      callable: formModel.callable as boolean,
      item: formModel.item as string,
      room: formModel.room as number,
      methodName: formModel.methodName as string,
      creation,
      owner
    });
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

}
