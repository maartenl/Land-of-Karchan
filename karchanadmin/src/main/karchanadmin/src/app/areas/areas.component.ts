import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { AreasRestService } from '../areas-rest.service';
import { Area } from './area.model';
import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';

@Component({
  selector: 'app-areas',
  templateUrl: './areas.component.html',
  styleUrls: ['./areas.component.css']
})
export class AreasComponent extends AdminComponent<Area, string> implements OnInit {
  form: FormGroup;

  constructor(
    private areasRestService: AreasRestService,
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
    this.setItemById(id);
  }

  getItems() {
    this.areasRestService.getAll()
      .subscribe({
        next: data => {
          this.items = data;
        }
      });
  }

  getRestService(): AdminRestService<Area, string> {
    return this.areasRestService;
  }

  setForm(item?: Area) {
    const object = item === undefined ? {
      area: '',
      description: '',
      shortdesc: '',
      owner: null,
      creation: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): Area {
    return new Area();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): Area {
    const formModel = this.form.value;

    // return new `Area` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const area = this.item === undefined ? null : this.item.area;
    const creation = this.item === undefined ? null : this.item.creation;
    const owner = this.item === undefined ? null : this.item.owner;
    const saveArea: Area = new Area({
      area: formModel.area as string,
      description: formModel.description as string,
      shortdesc: formModel.shortdesc as string,
      creation,
      owner: formModel.owner as string,
    });
    return saveArea;
  }

  setItemById(id: string) {
    this.areasRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setArea(data); }
      }
    });
    return false;
  }


  private setArea(area: Area) {
    this.item = area;
    this.form.reset({
      area: area.area,
      description: area.description,
      shortdesc: area.shortdesc,
      owner: area.owner
    });
  }

}
