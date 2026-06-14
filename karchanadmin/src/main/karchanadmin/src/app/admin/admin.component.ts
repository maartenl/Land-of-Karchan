import {AdminObject} from './admin-object.model';
import {AdminRestService} from './admin-rest.service';
import {ToastService} from '../toast.service';
import {ColDef, GridReadyEvent, RowSelectionOptions, SelectionChangedEvent, themeBalham} from 'ag-grid-community';
import {signal} from '@angular/core';
import {Logger} from '../consolelog.service';

/**
 * The idea here is to contain common functionality in this class,
 * which can be used in all administration pages, by extending
 * this class.
 */
export abstract class AdminComponent<T extends AdminObject<U>, U> {

  item = signal<T>(this.makeItem());

  rowData: T[] = [];

  protected readonly themeBalham = themeBalham;

  // abstract ngOnInit(): void;

  onGridReady(params: GridReadyEvent): void {
    Logger.logEntering("onGridReady");
    this.getItems();
  }

  abstract colDefs: ColDef[];

  abstract restService: AdminRestService<T, U>;

  abstract setForm(): void;

  abstract makeItem(): T;

  getItems(): void {
    this.restService.getAll()
      .subscribe({
        next: data => {
          this.rowData = data;
        }
      });
  }

  abstract toastService: ToastService;

  abstract onSelectionChanged(event: SelectionChangedEvent): void;

  public cancel(event: Event): void {
    event.preventDefault();
    this.setForm();
  }

  public isSelected() {
    return this.item !== undefined && this.item !== null;
  }

  public isActive(item: T) {
    if (item === undefined) {
      return '';
    }
    if (!this.isSelected()) {
      return '';
    }
    return (this.item().getIdentifier() === item?.getIdentifier()) ? 'table-active' : '';
  }

  refresh() {
    this.restService.clearCache();
    this.getItems();
  }

  public deleteItem(): void {
    if (this.item === null) {
      return;
    }
    Logger.log("delete item " + this.item().getIdentifier());
    this.restService.delete(this.item()).subscribe({
        next: (result: any) => { // on success
          this.rowData = this.rowData
            .filter((bl) => bl === undefined ||
              bl.getIdentifier() !== this.item().getIdentifier());
          this.rowData = [...this.rowData];
          this.toastService.showMessage(this.item().getType() + " " + this.item().getIdentifier() + " successfully deleted.", "Deleted...");
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );

  }

  abstract getForm(): T;

  abstract setItemById(id: U | undefined | null): boolean;

  public createItem(event: Event): void {
    event.preventDefault();
    const itemFromForm = this.getForm();
    this.restService.create(itemFromForm).subscribe(
      (result: U) => { // on success
        itemFromForm.setIdentifier(result);
        this.rowData.push(itemFromForm);
        this.rowData = [...this.rowData];
        this.setItemById(result);
        this.toastService.showMessage(itemFromForm.getType() + " " + itemFromForm.getIdentifier() + " successfully created.",
          "Created...");
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
    return;
  }

  public updateItem(event: Event): void {
    event.preventDefault();
    const itemFromForm = this.getForm();
    const index = this.rowData.findIndex(rm => rm !== null && rm.getIdentifier() === itemFromForm.getIdentifier());
    this.restService.update(itemFromForm).subscribe(
      (result: any) => { // on success
        this.rowData[index] = itemFromForm;
        this.rowData = [...this.rowData];
        this.toastService.showMessage(itemFromForm.getType() + " " + itemFromForm.getIdentifier() + " successfully updated.",
          "Updated...");
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

}
