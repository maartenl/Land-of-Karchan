import {Component, Input, OnInit} from '@angular/core';
import {Item} from "../items/item.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Logger} from "../consolelog.service";
import {ItemsRestService} from "../items-rest.service";
import {ToastService} from "../toast.service";

@Component({
  selector: 'app-item-sub',
  standalone: false,
  templateUrl: './item-sub.component.html',
  styleUrl: './item-sub.component.css'
})
export class ItemSubComponent implements OnInit {
  @Input() iteminstances: Item[] = [] = new Array<Item>(0);

  itemForm: FormGroup;

  constructor(private itemsRestService: ItemsRestService,
              private formBuilder: FormBuilder,
              private toastService: ToastService
  ) {
    const item = {
      id: null,
      itemid: null,
      containerid: null,
      containerdefid: null,
      belongsto: null,
      room: null,
      discriminator: null,
      shopkeeper: null,
      creation: null,
      owner: null
    }
    this.itemForm = this.formBuilder.group(item);
  }

  ngOnInit(): void {
  }


  deleteItemInstance(item: Item) {
    Logger.log('deleteItemInstance');
    this.itemsRestService.deleteIteminstance(item).subscribe({
        next: (result: any) => { // on success
          this.iteminstances = this.iteminstances
            .filter((bl) => bl === undefined ||
              bl.id !== item?.id);
          this.iteminstances = [...this.iteminstances];
          this.toastService.show(item.getType() + ' ' + item.id + ' successfully deleted.', {
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

  createIteminstance() {
    const formModel = this.itemForm.value;

    // return new `Item` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const item: Item = new Item({
      id: null,
      itemid: formModel.itemid as number,
      containerid: formModel.containerid as number,
      belongsto: formModel.belongsto as string,
      room: formModel.room as number,
      discriminator: formModel.discriminator as number,
      shopkeeper: formModel.shopkeeper as string,
      creation: null,
      owner: formModel.owner as string
    });
    this.itemsRestService.createIteminstance(item).subscribe(
      (result: any) => { // on success
        this.toastService.show(item.getType() + ' successfully created.', {
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
  }
}
