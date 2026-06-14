import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {AgGridAngular} from 'ag-grid-angular';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {Area} from './area.model';
import {ActivatedRoute} from '@angular/router';
import {ToastService} from '../toast.service';
import {AreasRestService} from '../areas-rest.service';
import {ColDef, GridReadyEvent, SelectionChangedEvent} from 'ag-grid-community';
import {Logger} from '../consolelog.service';
import { AdminRestService } from "../admin/admin-rest.service";
import {rowSelection} from '../aggrid.utils';

export interface AreaData {
  area: string,
  description: string,
  shortdesc: string,
  owner: string,
}

@Component({
  selector: 'app-areas',
  imports: [AgGridAngular, FormField],
  templateUrl: './areas.html',
  styleUrl: './areas.css',
})
export class Areas extends AdminComponent<Area, string> implements OnInit {
  override restService = inject(AreasRestService);
  private route = inject(ActivatedRoute);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "area", filter: true, width: 150},
    {field: "owner", filter: true, width: 100},
    {field: "creation"},
  ];

  areaModel = signal<AreaData>({
    area: '',
    description: '',
    shortdesc: '',
    owner: '',
  });

  form = form(this.areaModel)

  ngOnInit() {
    const id: string | null = this.route.snapshot.paramMap.get('id');
    if (id === null) {
      return;
    }
    this.route.params.subscribe(res => {
      if (res['id'] !== 0) this.setItemById(res['id']) // 'id' here is an example url parameter
    })
    this.setItemById(id);
  }

  override getItems() {
    this.restService.getAll()
      .subscribe({
        next: data => {
          this.rowData = data;
        }
      });
  }

  override setItemById(id: string | undefined | null) {
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
    const area = this.item();
    Logger.logEntering("setForm");
    this.areaModel.set({
      area: area.area ?? "",
      description: area.description  ?? "",
      owner: area.owner ?? "",
      shortdesc: area.shortdesc ?? "",
    })
  }

  override getForm(): Area {
    const area = this.item();
    const formModel = this.areaModel();
    area.area = formModel.area;
    area.description = formModel.description == "" ? null : formModel.description;
    area.shortdesc = formModel.shortdesc == "" ? null : formModel.shortdesc;
    area.owner = formModel.owner == "" ? null : formModel.owner;
    return area;
  }

  override makeItem(): Area {
    return new Area();
  }

  override onSelectionChanged = (event: SelectionChangedEvent) => {
    this.setItemById(event.api.getSelectedRows()[0].area);
  };

  protected readonly rowSelection = rowSelection;
}
