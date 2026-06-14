import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {ToastService} from '../../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {AdminComponent} from '../../admin/admin.component';
import {Sillyname} from '../sillynames.model';
import {SillynamesRestService} from '../../sillynames-rest.service';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../../aggrid.utils';
import {ReactiveFormsModule} from '@angular/forms';

export interface SillynameData {
  name: string;
}

@Component({
  selector: 'app-sillynames',
  imports: [AgGridAngular,
    FormField, ReactiveFormsModule],
  templateUrl: './sillynames.html',
  styleUrl: './sillynames.css',
})
export class Sillynames extends AdminComponent<Sillyname, string> {
  override restService = inject(SillynamesRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 100},
  ];

  sillynameModel = signal<SillynameData>({
    name: "",
  });

  form = form(this.sillynameModel);

  override setForm(): void {
    const sillyName = this.item();
    this.sillynameModel.set({
      name: sillyName.name ?? "",
    });
  }

  override makeItem(): Sillyname {
    return new Sillyname();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].name);
  }

  override getForm(): Sillyname {
    const Sillyname = this.item();
    const formModel = this.sillynameModel();
    Sillyname.name = formModel.name == "" ? null : formModel.name;
    return Sillyname;
  }

  override setItemById(id: string | null | undefined): boolean {
    if (id === undefined || id === null) {
      return false;
    }
    this.restService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.item.set(data);
          this.setForm();
        }
      }
    });
    return false;
  }

  protected readonly rowSelection = rowSelection;
}
