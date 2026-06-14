import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {Worldattribute} from './worldattribute.model';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {rowSelection} from '../aggrid.utils';
import {ToastService} from '../toast.service';
import {WorldattributesRestService} from '../worldattributes-rest.service';
import {AgGridAngular} from 'ag-grid-angular';

export interface WorldattributeData {
  name: string;
  type: string;
  contents: string;
  owner: string;
}

@Component({
  selector: 'app-worldattributes',
  imports: [
    AgGridAngular, FormField
  ],
  templateUrl: './worldattributes.html',
  styleUrl: './worldattributes.css',
})
export class Worldattributes extends AdminComponent<Worldattribute, string> {

  override restService = inject(WorldattributesRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 150},
    {field: "type", filter: true, width: 100},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  worldattributeModel = signal<WorldattributeData>({
    name: "",
    type: "",
    contents: "",
    owner: ""
  });

  form = form(this.worldattributeModel);

  override setForm(): void {
    const method = this.item();
    this.worldattributeModel.set({
      name: method.name ?? "",
      type: method.type ?? "",
      contents: method.contents ?? "",
      owner: method.owner ?? ""
    });
  }

  override makeItem(): Worldattribute {
    return new Worldattribute();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].name);
  }

  override getForm(): Worldattribute {
    const method = this.item();
    const formModel = this.worldattributeModel();
    method.name = formModel.name;
    method.type = formModel.type == "" ? null : formModel.type;
    method.contents = formModel.contents == "" ? null : formModel.contents;
    method.owner = formModel.owner == "" ? null : formModel.owner;
    return method;
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
