import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {Method} from './method.model';
import {AdminComponent} from '../admin/admin.component';
import {ToastService} from '../toast.service';
import {MethodsRestService} from '../methods-rest.service';
import {ActivatedRoute} from '@angular/router';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {Command} from '../commands/command.model';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';

export interface MethodData {
  name: string;
  src: string;
  owner: string;
}

@Component({
  selector: 'app-methods',
  imports: [
    AgGridAngular, FormField
  ],
  templateUrl: './methods.html',
  styleUrl: './methods.css',
})
export class Methods extends AdminComponent<Method, string> implements OnInit {

  override restService = inject(MethodsRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "name", filter: true, width: 150},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  commandsColDefs: ColDef[] = [
    {field: "id", filter: true, width: 100, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/commands"}},
    {field: "command", filter: true, width: 150},
  ];

  methodModel = signal<MethodData>({
    name: "",
    src: "",
    owner: ""
  });

  form = form(this.methodModel);

  commands = signal<Command[]>([]);

  ngOnInit() {
    const name: string | null = this.route.snapshot.paramMap.get('name');
    if (name === null) {
      return;
    }
    this.setItemById(name);
  }

  override setForm(): void {
    const method = this.item();
    this.methodModel.set({
      name: method.name ?? "",
      src: method.src ?? "",
      owner: method.owner ?? ""
    });
  }

  override makeItem(): Method {
    return new Method();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].name);
  }

  override getForm(): Method {
    const method = this.item();
    const formModel = this.methodModel();
    method.name = formModel.name;
    method.src = formModel.src;
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
    this.restService.getCommands(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.commands.set(data);
        }
      }
    });
    return false;
  }

  protected readonly rowSelection = rowSelection;
}
