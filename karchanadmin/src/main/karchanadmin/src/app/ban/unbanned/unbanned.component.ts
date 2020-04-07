import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { ToastService } from '../../toast.service';
import { BanRestService } from 'src/app/ban-rest.service';
import { Unbannedname } from '../unbanned.model';

@Component({
  selector: 'app-unbanned',
  templateUrl: './unbanned.component.html',
  styleUrls: ['./unbanned.component.css']
})
export class UnbannedComponent implements OnInit {

  items: Unbannedname[];

  item: Unbannedname;

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

  setForm(item?: Unbannedname) {
    const object = item === undefined ? {
      name: ''
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  setItem(item: Unbannedname) {
    this.item = item;
    this.setForm(item);
  }

  getForm(): Unbannedname {
    const formModel = this.form.value;

    // return new `Unbannedname` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveMethod: Unbannedname = new Unbannedname({
      name: formModel.name as string
    });
    return saveMethod;
  }

  getItems() {
    this.banRestService.getUnbannednames().subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.items = data;
          this.items = this.items.map((x) => new Unbannedname(x));
        }
      }
    });
    return false;
  }

  makeItem(): Unbannedname {
    return new Unbannedname();
  }

  public createItem(): void {
    const itemFromForm = this.getForm();
    this.banRestService.createUnbannedname(itemFromForm).subscribe(
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
    this.banRestService.deleteUnbannedname(this.item).subscribe(
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
