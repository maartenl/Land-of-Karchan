import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { ManpagesRestService } from '../manpages-rest.service';
import { Manpage } from './manpage.model';
import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';

@Component({
  selector: 'app-manpages',
  templateUrl: './manpages.component.html',
  styleUrls: ['./manpages.component.css']
})
export class ManpagesComponent extends AdminComponent<Manpage, string> implements OnInit {
  form: FormGroup;

  constructor(
    private manpagesRestService: ManpagesRestService,
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
    this.manpagesRestService.getAll()
      .subscribe({
        next: data => {
          this.items = data;
        }
      });
  }

  getRestService(): AdminRestService<Manpage, string> {
    return this.manpagesRestService;
  }

  setForm(item?: Manpage) {
    const object = item === undefined ? {
      command: null,
      contents: null,
      synopsis: null,
      seealso: null,
      example1: null,
      example1a: null,
      example1b: null,
      example2: null,
      example2a: null,
      example2b: null,
      example2c: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): Manpage {
    return new Manpage();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): Manpage {
    const formModel = this.form.value;

    const saveManpage: Manpage = new Manpage({
      command: formModel.command as string,
      contents: formModel.contents as string,
      synopsis: formModel.synopsis as string,
      seealso: formModel.seealso as string,
      example1: formModel.example1 as string,
      example1a: formModel.example1a as string,
      example1b: formModel.example1b as string,
      example2: formModel.example2 as string,
      example2a: formModel.example2a as string,
      example2b: formModel.example2b as string,
      example2c: formModel.example2c as string,
    });
    return saveManpage;
  }

  setItemById(id: string) {
    this.manpagesRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setManpage(data); }
      }
    });
    return false;
  }


  private setManpage(manpage: Manpage) {
    this.item = manpage;
    this.form.reset({
      command: manpage.command,
      contents: manpage.contents,
      synopsis: manpage.synopsis,
      seealso: manpage.seealso,
      example1: manpage.example1,
      example1a: manpage.example1a,
      example1b: manpage.example1b,
      example2: manpage.example2,
      example2a: manpage.example2a,
      example2b: manpage.example2b,
      example2c: manpage.example2c
    });
  }


}
