import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { ToastService } from '../../toast.service';
import { Bannedname } from '../bannednames.model';
import { BanRestService } from 'src/app/ban-rest.service';

@Component({
  selector: 'app-bannednames',
  templateUrl: './bannednames.component.html',
  styleUrls: ['./bannednames.component.css']
})
export class BannednamesComponent implements OnInit {
  items: Bannedname[];

  item: Bannedname;

  form: FormGroup;

  constructor(
    private banRestService: BanRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    this.setForm();
    this.item = this.makeItem();
    this.getItems();
  }

  ngOnInit() {
  }

  setForm(item?: Bannedname) {
    const object = item === undefined ? {
      address: '',
      days: null,
      IP: null,
      name: null,
      deputy: null,
      date: null,
      reason: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  setItem(item: Bannedname) {
    this.item = item;
    this.setForm(item);
  }

  getForm(): Bannedname {
    const formModel = this.form.value;

    // return new `Bannedname` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveMethod: Bannedname = new Bannedname({
      address: formModel.address as string,
      days: formModel.days as string,
      IP: formModel.IP as string,
      name: formModel.name as string,
      deputy: formModel.deputy as string,
      date: formModel.date as string,
      reason: formModel.reason as string
    });
    return saveMethod;
  }

  getItems() {
    this.banRestService.getBannednames().subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.items = data;
          this.items = this.items.map((x) => new Bannedname(x));
        }
      }
    });
    return false;
  }

  makeItem(): Bannedname {
    return new Bannedname();
  }

  public createItem(): void {
    const itemFromForm = this.getForm();
    this.banRestService.createBannedname(itemFromForm).subscribe(
      (result: any) => { // on success
        itemFromForm.setIdentifier(result);
        this.items.push(itemFromForm);
        this.items = [...this.items];
        this.toastService.show(this.item.getType() + ' ' + this.item.getIdentifier() + ' successfully created.', {
          delay: 3000,
          autohide: true,
          headertext: 'Created...'
        });
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
    return;
  }

  public deleteItem(): void {
    if (!this.item) {
      if (window.console) {
        console.log('delete item is leeg');
      }
      return;
    }
    this.banRestService.deleteBannedname(this.item).subscribe(
      (result: any) => { // on success
        this.items = this.items
          .filter((bl) => bl === undefined ||
            bl.getIdentifier() !== this.item.getIdentifier());
        this.items = [...this.items];
        this.toastService.show(this.item.getType() + ' ' + this.item.getIdentifier() + ' successfully deleted.', {
          delay: 3000,
          autohide: true,
          headertext: 'Deleted...'
        });
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );

  }

  public cancel() {
    this.item = this.makeItem();
    this.setForm(this.item);
  }

}
