import {Component, inject, input, InputSignal, signal} from '@angular/core';
import {Item} from '../items/item.model';
import {form, FormField} from '@angular/forms/signals';
import {ToastService} from '../toast.service';
import {ItemsRestService} from '../items-rest.service';
import {Logger} from '../consolelog.service';
import {AdminMudType} from '../adminmudtype';
import {AgGridAngular} from 'ag-grid-angular';
import {ColDef, GridReadyEvent, RowSelectionOptions, SelectionChangedEvent, themeBalham} from 'ag-grid-community';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {rowSelection} from '../aggrid.utils';

export interface ItemModel {
  id: number | null;
  itemid: number | null;
  containerid: number | null;
  containerdefid: number | null;
  belongsto: string;
  room: number | null;
  discriminator: number | null;
  shopkeeper: string;
  owner: string;
}

@Component({
  selector: 'app-item-sub',
  imports: [FormField, AgGridAngular],
  templateUrl: './item-sub.html',
  styleUrl: './item-sub.css',
})
export class ItemSub {

  private itemsRestService = inject(ItemsRestService)
  private toastService = inject(ToastService)

  mudType: InputSignal<AdminMudType> = input.required();

  iteminstances = signal<Item[]>([]);

  // Column Definitions: Defines & controls grid columns.
  colDefs: ColDef[] = [
    {field: "id", headerName: "item", width: 100},
    {field: "description", filter: true, width: 200},
    {field: "itemid", headerName: "Item definition", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/items"}},
    {field: "belongsto", headerName: "Belongs to", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/characters"}},
    {field: "containerid", headerName: "Contained in", filter: true, width: 100},
    {field: "containerdefid", headerName: "Container def", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/items"}},
    {field: "room", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/rooms"}},
    {field: "discriminator", width: 20},
    {field: "shopkeeper", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/characters"}},
    {field: "owner", filter: true, width: 100},
  ];

  itemModel = signal<ItemModel>({
    belongsto: "",
    containerdefid: null,
    containerid: null,
    discriminator: null,
    id: null,
    itemid: null,
    owner: "",
    room: null,
    shopkeeper: ""
  })

  itemForm = form(this.itemModel)

  getItemsOfCharacter(name: string) {
    this.itemsRestService.getAllItemsOfCharacter(name).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.iteminstances.set(data);
        }
      }
    });
  }

  getItemsOfRoom(id: number) {
    this.itemsRestService.getAllItemsInRoom(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.iteminstances.set(data);
        }
      }
    });
  }

  getItemsOfItemdefinition(itemdefinitionid: number) {
    this.itemsRestService.getAllItemsOfItemdefinition(itemdefinitionid).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.iteminstances.set(data);
        }
      }
    });
  }

  deleteItemInstance(): boolean {
    Logger.log('deleteItemInstance');
    const formModel = this.itemModel();
    const item: Item = new Item({
      id: formModel.id,
      itemid: formModel.itemid,
      containerid: formModel.containerid,
      belongsto: formModel.belongsto === "" ? null : formModel.belongsto,
      room: formModel.room,
      discriminator: formModel.discriminator,
      shopkeeper: formModel.shopkeeper === "" ? null : formModel.shopkeeper,
      creation: null,
      owner: formModel.owner === "" ? null : formModel.owner
    });
    this.itemsRestService.deleteIteminstance(item).subscribe({
        next: (result: any) => { // on success
          this.iteminstances.update(iteminstances => {
            iteminstances
              .filter((bl) => bl === undefined ||
                bl.id !== item?.id);
            iteminstances = [...iteminstances];
            this.toastService.showMessage(item.getType() + " " + item.id + " successfully deleted.", "Deleted...");
            return iteminstances;
          });
        },
        error:
          (err: any) => { // error
            // console.log('error', err);
          },
        complete:
          () => { // on completion
          }
      }
    );
    return false;
  }

  createIteminstance(event: Event) {
    event.preventDefault();
    const formModel = this.itemModel();

    // return new `Item` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const item: Item = new Item({
      id: null,
      itemid: formModel.itemid,
      containerid: formModel.containerid,
      belongsto: formModel.belongsto == "" ? null : formModel.belongsto,
      room: formModel.room,
      discriminator: formModel.discriminator,
      shopkeeper: formModel.shopkeeper == "" ? null : formModel.shopkeeper,
      creation: null,
      owner: formModel.owner == "" ? null : formModel.owner,
    });
    this.itemsRestService.createIteminstance(item).subscribe(
      (result: any) => { // on success
        this.toastService.showMessage(item.getType() + " successfully created.", "Created...");
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  onSelectionChanged = (event: SelectionChangedEvent) => {
    const item: Item = event.api.getSelectedRows()[0];
    this.itemModel.set({
      belongsto: item.belongsto ?? "",
      containerdefid:item.containerdefid ?? null,
      containerid: item.containerid ?? null,
      discriminator: item.discriminator ?? null,
      id: item.id ?? null,
      itemid: item.itemid ?? null,
      owner: item.owner ?? "",
      room: item.room ?? null,
      shopkeeper: item.shopkeeper ?? ""
    })
  };

  protected readonly themeBalham = themeBalham;
  protected readonly rowSelection = rowSelection;
}
