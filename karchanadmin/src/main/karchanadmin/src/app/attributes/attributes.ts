import {Component, inject, OnInit, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {ActivatedRoute} from '@angular/router';
import {ToastService} from '../toast.service';
import {AttributesRestService} from '../attributes-rest.service';
import {Attribute} from '../attribute.model';
import {ColDef, GridReadyEvent, SelectionChangedEvent} from 'ag-grid-community';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';
import {Logger} from '../consolelog.service';
import {MudCharacter} from '../characters/character.model';

export interface AttributeData {
  id: number | null;
  name: string;
  value: string;
  valueType: string;
}

@Component({
  selector: 'app-attributes',
  imports: [AgGridAngular, FormField],
  templateUrl: './attributes.html',
  styleUrl: './attributes.css',
})
export class Attributes extends AdminComponent<Attribute, number> implements OnInit {
  override restService = inject(AttributesRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "id", width: 150},
    {field: "name", filter: true, width: 150},
    {field: "value", filter: true, width: 150},
    {field: "valueType", width: 150},
    {field: "objecttype", width: 150},
    {field: "objectid", width: 150},
  ];

  name = signal<string>("");

  attributeModel = signal<AttributeData>({
    id: null,
    name: "",
    valueType: "",
    value: ""
  })

  searchForm = form<string>(this.name);

  form = form<AttributeData>(this.attributeModel);

  ngOnInit(): void {
    if (window.console) {
      console.log('ngOnInit');
    }
    const name: string | null = this.route.snapshot.paramMap.get('name');
    if (name === undefined || name === null) {
      return;
    }
    this.name.set(name);
  }

  override getItems() {
    if (this.name() == "") {
      return;
    }
    this.restService.getAllWithName(this.name()).subscribe({
      next: (data: Attribute[]) => {
        this.rowData = data;
      }
    });
  }

  override makeItem(): Attribute {
    return new Attribute();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].id);
  }

  override setForm() {
    const attribute = this.item();
    this.attributeModel.set({
      id: attribute.id ?? null,
      name: attribute.name ?? "",
      valueType: attribute.valueType ?? "",
      value: attribute.value ?? "",
    });
  }

  override getForm(): Attribute {
    const attribute = this.item();
    const formModel = this.attributeModel();
    attribute.id = formModel.id;
    attribute.name = formModel.name;
    attribute.valueType = formModel.valueType;
    attribute.value = formModel.value == null ? null : formModel.value;
    return attribute;
  }

  override setItemById(id: number | undefined | null): boolean {
    if (id === undefined || id === null) {
      return false;
    }
    this.restService.get(id).subscribe({
      next: (data: Attribute) => {
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
