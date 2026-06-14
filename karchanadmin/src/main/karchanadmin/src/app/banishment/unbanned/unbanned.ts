import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../../admin/admin.component';
import {ToastService} from '../../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {rowSelection} from '../../aggrid.utils';
import {AgGridAngular} from 'ag-grid-angular';
import {UnbannednameRestService} from '../../unbanned-rest.service';
import {Unbannedname} from '../unbannedname.model';
import {ReactiveFormsModule} from '@angular/forms';


export interface UnUnbannednameData {
  name: string;
}

@Component({
  selector: 'app-unbanned',
  imports: [
    AgGridAngular,
    FormField,
    ReactiveFormsModule
  ],
  templateUrl: './unbanned.html',
  styleUrl: './unbanned.css',
})
export class Unbanned extends AdminComponent<Unbannedname, string> {
  override restService = inject(UnbannednameRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 150},
  ];

  UnbannednameModel = signal<UnUnbannednameData>({
    name: "",
  });

  form = form(this.UnbannednameModel);

  override setForm(): void {
    const Unbannedname = this.item();
    this.UnbannednameModel.set({
      name: Unbannedname.name ?? "",
    });
  }

  override makeItem(): Unbannedname {
    return new Unbannedname();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].name);
  }

  override getForm(): Unbannedname {
    const Unbannedname = this.item();
    const formModel = this.UnbannednameModel();
    Unbannedname.name = formModel.name == "" ? null : formModel.name;
    return Unbannedname;
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
