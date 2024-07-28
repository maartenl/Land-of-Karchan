import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {ToastService} from '../../toast.service';
import {BannedIP} from '../banned.model';
import {BanRestService} from 'src/app/ban-rest.service';

@Component({
  selector: 'app-banned',
  templateUrl: './banned.component.html',
  styleUrls: ['./banned.component.css']
})
export class BannedComponent implements OnInit {
  items: BannedIP[] = new Array<BannedIP>(0);

  item: BannedIP | null = null;

  form: FormGroup;

  constructor(
    private banRestService: BanRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    const object = {
      address: '',
      days: null,
      IP: null,
      name: null,
      deputy: null,
      date: null,
      reason: null
    };
    this.form = this.formBuilder.group(object);
    this.item = this.makeItem();
    this.getItems();
  }

  ngOnInit() {
  }

  setForm(item?: BannedIP) {
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

  setItem(item: BannedIP) {
    this.item = item;
    this.setForm(item);
  }

  getForm(): BannedIP {
    const formModel = this.form.value;

    // return new `BannedIP` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveMethod: BannedIP = new BannedIP({
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
    this.banRestService.getBannedIPs().subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.items = data;
          this.items = this.items.map((x) => new BannedIP(x));
        }
      }
    });
    return false;
  }

  makeItem(): BannedIP {
    return new BannedIP();
  }

  public createItem(): void {
    const itemFromForm = this.getForm();
    this.banRestService.createBannedIP(itemFromForm).subscribe({
        next: (result: any) => { // on success
          itemFromForm.setIdentifier(result);
          this.items.push(itemFromForm);
          this.items = [...this.items];
          this.toastService.show(this.item?.getType() + ' ' + this.item?.getIdentifier() + ' successfully created.', {
            delay: 3000,
            autohide: true,
            headertext: 'Created...'
          });
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
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
    this.banRestService.deleteBannedIP(this.item).subscribe({
        next: (result: any) => { // on success
          this.items = this.items
            .filter((bl) => bl === undefined ||
              bl?.getIdentifier() !== this.item?.getIdentifier());
          this.items = [...this.items];
          this.toastService.show(this.item?.getType() + ' ' + this.item?.getIdentifier() + ' successfully deleted.', {
            delay: 3000,
            autohide: true,
            headertext: 'Deleted...'
          });
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );

  }

  public cancel() {
    this.item = this.makeItem();
    this.setForm(this.item);
  }
}
