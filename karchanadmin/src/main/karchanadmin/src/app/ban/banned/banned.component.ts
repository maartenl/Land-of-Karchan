import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { ToastService } from '../../toast.service';
import { BannedIP } from '../banned.model';
import { BanRestService } from 'src/app/ban-rest.service';

@Component({
  selector: 'app-banned',
  templateUrl: './banned.component.html',
  styleUrls: ['./banned.component.css']
})
export class BannedComponent implements OnInit {
  items: BannedIP[];

  item: BannedIP;

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
    this.banRestService.createBannedIP(itemFromForm).subscribe(
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
    this.banRestService.deleteBannedIP(this.item).subscribe(
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
