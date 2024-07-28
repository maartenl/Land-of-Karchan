import {AdminObject} from './admin-object.model';
import {AdminRestService} from './admin-rest.service';
import {ToastService} from '../toast.service';

/**
 * The idea here is to contain common functionality in this class,
 * which can be used in all administration pages, by extending
 * this class.
 */
export abstract class AdminComponent<T extends AdminObject<U>, U> {

  item: T | null = null;

  items: T[] = new Array<T>(0);

  abstract getRestService(): AdminRestService<T, U>;

  abstract setForm(item?: T): void;

  abstract makeItem(): T;

  abstract getItems(): void;

  abstract getToastService(): ToastService;

  public cancel(): void {
    this.setForm();
    this.item = null;
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
    return (this.item?.getIdentifier() === item?.getIdentifier()) ? 'table-active' : '';
  }

  refresh() {
    this.getRestService().clearCache();
    this.getItems();
  }

  public deleteItem(): void {
    if (this.item === null) {
      return;
    }
    if (window.console) {
      console.log('delete item ' + this.item.getIdentifier());
    }
    this.getRestService().delete(this.item).subscribe({
        next: (result: any) => { // on success
          this.items = this.items
            .filter((bl) => bl === undefined ||
              bl.getIdentifier() !== this.item?.getIdentifier());
          this.items = [...this.items];
          this.getToastService().show(this.item?.getType() + ' ' + this.item?.getIdentifier() + ' successfully deleted.', {
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

  abstract getForm(): T;

  abstract setItemById(id: U | undefined | null): boolean;

  public createItem(): void {
    const itemFromForm = this.getForm();
    this.getRestService().create(itemFromForm).subscribe(
      (result: U) => { // on success
        itemFromForm.setIdentifier(result);
        this.items.push(itemFromForm);
        this.items = [...this.items];
        this.setItemById(result);
        this.getToastService().show(itemFromForm.getType() + ' ' + itemFromForm.getIdentifier() + ' successfully created.', {
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

  public updateItem(): void {
    const itemFromForm = this.getForm();
    const index = this.items.findIndex(rm => rm !== null && rm.getIdentifier() === itemFromForm.getIdentifier());
    this.getRestService().update(itemFromForm).subscribe(
      (result: any) => { // on success
        this.items[index] = itemFromForm;
        this.items = [...this.items];
        this.getToastService().show(itemFromForm.getType() + ' ' + itemFromForm.getIdentifier() + ' successfully updated.', {
          delay: 3000,
          autohide: true,
          headertext: 'Updated...'
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
