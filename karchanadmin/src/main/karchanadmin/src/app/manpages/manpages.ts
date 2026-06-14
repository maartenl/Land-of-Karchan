import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {Manpage} from './manpage.model';
import {AdminComponent} from '../admin/admin.component';
import {ActivatedRoute} from '@angular/router';
import {ToastService} from '../toast.service';
import {ManpagesRestService} from '../manpages-rest.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {rowSelection} from '../aggrid.utils';
import {AdminMudType} from '../adminmudtype';
import {AgGridAngular} from 'ag-grid-angular';

export interface ManpageData {
  command: string;
  contents: string;
  synopsis: string;
  seealso: string;
  example1: string;
  example1a: string;
  example1b: string;
  example2: string;
  example2a: string;
  example2b: string;
  example2c: string;
}

@Component({
  selector: 'app-manpages',
  imports: [
    AgGridAngular, FormField
  ],
  templateUrl: './manpages.html',
  styleUrl: './manpages.css',
})
export class Manpages extends AdminComponent<Manpage, string> {
  override restService = inject(ManpagesRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "command", filter: true, width: 150},
  ];

  manpageModel = signal<ManpageData>({
    command: "",
    contents: "",
    synopsis: "",
    seealso: "",
    example1: "",
    example1a: "",
    example1b: "",
    example2: "",
    example2a: "",
    example2b: "",
    example2c: ""
  })

  form = form(this.manpageModel);

  setItemById(id: string | undefined | null) {
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

  override setForm(): void {
    const itemDefinition = this.item();
    this.manpageModel.set({
      command: itemDefinition.command ?? "",
      contents: itemDefinition.contents ?? "",
      synopsis: itemDefinition.synopsis ?? "",
      seealso: itemDefinition.seealso ?? "",
      example1: itemDefinition.example1 ?? "",
      example1a: itemDefinition.example1a ?? "",
      example1b: itemDefinition.example1b ?? "",
      example2: itemDefinition.example2 ?? "",
      example2a: itemDefinition.example2a ?? "",
      example2b: itemDefinition.example2b ?? "",
      example2c: itemDefinition.example2c ?? ""
    });
  }

  override getForm(): Manpage {
    const manpage = this.item();
    const formModel = this.manpageModel();
    manpage.command = formModel.command;
    manpage.contents = formModel.contents;
    manpage.synopsis = formModel.synopsis;
    manpage.seealso = formModel.seealso == "" ? null : formModel.seealso;
    manpage.example1 = formModel.example1 == "" ? null : formModel.example1;
    manpage.example1a = formModel.example1a == "" ? null : formModel.example1a;
    manpage.example1b = formModel.example1b == "" ? null : formModel.example1b;
    manpage.example2 = formModel.example2 == "" ? null : formModel.example2;
    manpage.example2a = formModel.example2a == "" ? null : formModel.example2a;
    manpage.example2b = formModel.example2b == "" ? null : formModel.example2b;
    manpage.example2c = formModel.example2c == "" ? null : formModel.example2c;
    return manpage
  }

  override makeItem(): Manpage {
    return new Manpage();
  }

  override onSelectionChanged = (event: SelectionChangedEvent) => {
    this.setItemById(event.api.getSelectedRows()[0].command);
  };

  protected readonly rowSelection = rowSelection;
  protected readonly AdminMudType = AdminMudType;
}
