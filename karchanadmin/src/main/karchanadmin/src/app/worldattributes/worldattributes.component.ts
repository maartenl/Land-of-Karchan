import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { WorldattributesRestService } from '../worldattributes-rest.service';
import { Worldattribute } from './worldattribute.model';
import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';

@Component({
  selector: 'app-worldattributes',
  templateUrl: './worldattributes.component.html',
  styleUrls: ['./worldattributes.component.css']
})
export class WorldattributesComponent extends AdminComponent<Worldattribute, string> implements OnInit {

  form: FormGroup;

  constructor(
    private worldattributesRestService: WorldattributesRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    this.setForm();
    this.makeItem();
    this.getItems();
  }

  ngOnInit() {
  }

  getItems() {
    this.worldattributesRestService.getAll()
      .subscribe({
        next: data => {
          this.items = data;
        }
      });
  }

  getRestService(): AdminRestService<Worldattribute, string> {
    return this.worldattributesRestService;
  }

  setForm(item?: Worldattribute) {
    const object = item === undefined ? {
      name: null,
      type: null,
      contents: null,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): Worldattribute {
    return new Worldattribute();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): Worldattribute {
    const formModel = this.form.value;

    const saveWorldattribute: Worldattribute = new Worldattribute({
      name: formModel.name as string,
      contents: formModel.contents as string,
      type: formModel.type as string,
      owner: formModel.owner as string
    });
    return saveWorldattribute;
  }

  setItemById(id: string) {
    this.worldattributesRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setWorldattribute(data); }
      }
    });
    return false;
  }

  private setWorldattribute(worldattribute: Worldattribute) {
    this.item = worldattribute;
    this.form.reset({
      name: worldattribute.name,
      contents: worldattribute.contents,
      type: worldattribute.type,
      owner: worldattribute.owner
    });
  }




}

