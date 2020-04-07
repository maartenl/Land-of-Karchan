import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { ToastService } from '../../toast.service';
import { BanRestService } from 'src/app/ban-rest.service';
import { Sillyname } from '../sillynames.model';

@Component({
  selector: 'app-sillynames',
  templateUrl: './sillynames.component.html',
  styleUrls: ['./sillynames.component.css']
})
export class SillynamesComponent implements OnInit {
  items: Sillyname[];

  item: Sillyname;

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

  setForm(item?: Sillyname) {
    const object = item === undefined ? {
      name: ''
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  setItem(item: Sillyname) {
    this.item = item;
    this.setForm(item);
  }

  getForm(): Sillyname {
    const formModel = this.form.value;

    // return new `Sillyname` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveMethod: Sillyname = new Sillyname({
      name: formModel.name as string
    });
    return saveMethod;
  }

  getItems() {
    this.banRestService.getSillynames().subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.items = data;
          this.items = this.items.map((x) => new Sillyname(x));
        }
      }
    });
    return false;
  }

  makeItem(): Sillyname {
    return new Sillyname();
  }

  public createItem(): void {
    const itemFromForm = this.getForm();
    this.banRestService.createSillyname(itemFromForm).subscribe(
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
    this.banRestService.deleteSillyname(this.item).subscribe(
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
