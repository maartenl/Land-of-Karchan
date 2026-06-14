import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {ToastService} from '../../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {AdminComponent} from '../../admin/admin.component';
import {Bannedname} from '../bannednames.model';
import {rowSelection} from '../../aggrid.utils';
import {BannednamesRestService} from '../../bannednames-rest.service';
import {AgGridAngular} from 'ag-grid-angular';

export interface BannednameData {
  name: string;
  days: number | null;
  reason: string;
  deputy: string;
}

@Component({
  selector: 'app-bannednames',
  imports: [
    AgGridAngular, FormField
  ],
  templateUrl: './bannednames.html',
  styleUrl: './bannednames.css',
})
export class Bannednames extends AdminComponent<Bannedname, string> {
  override restService = inject(BannednamesRestService,);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 150},
    {field: "deputy", filter: true, width: 100},
    {field: "creation", width: 100},
  ];

  bannednameModel = signal<BannednameData>({
    name: "",
    days: null,
    reason: "",
    deputy: "",
  });

  form = form(this.bannednameModel);

  override setForm(): void {
    const bannedName = this.item();
    this.bannednameModel.set({
      name: bannedName.name ?? "",
      days: bannedName.days ?? null,
      reason: bannedName.reason ?? "",
      deputy: bannedName.deputy ?? "",
    });
  }

  override makeItem(): Bannedname {
    return new Bannedname();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].name);
  }

  override getForm(): Bannedname {
    const bannedName = this.item();
    const formModel = this.bannednameModel();
    bannedName.name = formModel.name == "" ? null : formModel.name;
    bannedName.days = formModel.days;
    bannedName.reason = formModel.reason == "" ? null : formModel.reason;
    bannedName.deputy = formModel.deputy == "" ? null : formModel.deputy;
    return bannedName;
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
