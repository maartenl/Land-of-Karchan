import {Component, inject, input, InputSignal, signal} from '@angular/core';
import {Attribute} from '../attribute.model';
import {AttributesRestService} from '../attributes-rest.service';
import {ToastService} from '../toast.service';
import {form, FormField} from '@angular/forms/signals';
import {AgGridAngular} from 'ag-grid-angular';
import {ColDef, SelectionChangedEvent, themeBalham} from 'ag-grid-community';
import {AdminMudType} from '../adminmudtype';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {Logger} from '../consolelog.service';
import {rowSelection} from '../aggrid.utils';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {identity} from 'rxjs';

export interface AttributeModel {
  id: number;
  name: string;
  value: string;
  valueType: string;
}

@Component({
  selector: 'app-attributes-sub',
  imports: [FormField, AgGridAngular, FormsModule, ReactiveFormsModule],
  templateUrl: './attributes-sub.html',
  styleUrl: './attributes-sub.css',
})
export class AttributesSub {

  private attributesRestService = inject(AttributesRestService)
  private toastService = inject(ToastService)

  mudType: InputSignal<AdminMudType> = input.required();

  objectId = "";

  newid = -1;

  attributeinstances = signal<Attribute[]>([]);

  attribute = signal<Attribute>(new Attribute());

  // Column Definitions: Defines & controls grid columns.
  colDefs: ColDef[] = [
    {field: "id", width: 100},
    {field: "name", width: 200, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/attributes"}},
    {field: "value", width: 100},
    {field: "valueType", width: 100},
  ];

  attributeModel = signal<AttributeModel>({id: 0, name: "", value: "", valueType: ""});

  attributeForm = form(this.attributeModel);

  getAttributesOfCharacter(name: string) {
    this.objectId = name;
    this.attributesRestService.getFromCharacter(name).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.attributeinstances.set(data);
        }
      }
    });
  }

  getAttributesOfRoom(id: number) {
    this.objectId = id.toString();
    this.attributesRestService.getFromRoom(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.attributeinstances.set(data);
        }
      }
    });
  }

  deleteAttribute() {
    const attributeModel = this.attributeModel();
    const attribute: Attribute = this.attribute();
    attribute.id = attributeModel.id == 0 ? null : attributeModel.id;
    attribute.name = attributeModel.name == "" ? null : attributeModel.name;
    attribute.value = attributeModel.value == "" ? null : attributeModel.value;
    attribute.valueType =
      attributeModel.valueType == "" ? null : attributeModel.valueType;
    Logger.logEntering("deleteAttribute");
    this.attributesRestService.delete(attribute).subscribe(
      (result: any) => { // on success
        const index = this.attributeinstances().indexOf(attribute, 0);
        if (index > -1) {
          this.attributeinstances.update(attributes => {
            attributes.splice(index, 1);
            return attributes;
          });
        }
        this.toastService.showMessage("Attribute successfully deleted.", "Deleted...");
      }
    );
    return false;
  }

  updateAttribute(event: Event) {
    event.preventDefault();
    const attributeModel = this.attributeModel();
    const attribute: Attribute = this.attribute();
    attribute.id = attributeModel.id == 0 ? null : attributeModel.id;
    attribute.name = attributeModel.name == "" ? null : attributeModel.name;
    attribute.value = attributeModel.value == "" ? null : attributeModel.value;
    attribute.valueType =
      attributeModel.valueType == "" ? null : attributeModel.valueType;
    Logger.logEntering("updateAttribute");
    this.attributesRestService.update(attribute).subscribe(
      (result: any) => { // on success
        this.attributeinstances.update(attributes => {
          attributes.map(u => u.id !== attribute.id ? u : attribute);
          return attributes;
        });
        this.toastService.showMessage("Attribute successfully updated.", "Updated...");
      }
    );
    return false;
  }

  createAttribute() {
    const attributeModel = this.attributeModel();
    const attribute: Attribute = new Attribute({
      id: null,
      name: attributeModel.name == "" ? null : attributeModel.name,
      value: attributeModel.value == "" ? null : attributeModel.value,
      valueType: attributeModel.valueType == "" ? null : attributeModel.valueType,
      objecttype: this.mudType,
      objectid: this.objectId,
    });
    Logger.logEntering("createAttribute");
    attribute.id = null;
    this.attributesRestService.update(attribute).subscribe(
      (result: any) => { // on success
        this.attributeinstances.update(attributes => {
          attribute.id = this.newid;
          this.newid--;
          attributes.push(attribute);
          return attributes;
        });
        this.toastService.showMessage("Attribute successfully created.", "Created...");
      }
    );
    return false;
  }

  onSelectionChanged = (event: SelectionChangedEvent) => {
    const attribute: Attribute = event.api.getSelectedRows()[0];
    Logger.logObject(attribute);
    this.attribute.set(attribute);
    let attributeModel: AttributeModel = {
      id: attribute.id ?? 0,
      name: attribute.name ?? "",
      value: attribute.value ?? "",
      valueType: attribute.valueType ?? ""
    };
    this.attributeModel.set(attributeModel)
  };

  protected readonly themeBalham = themeBalham;
  protected readonly rowSelection = rowSelection;
}
