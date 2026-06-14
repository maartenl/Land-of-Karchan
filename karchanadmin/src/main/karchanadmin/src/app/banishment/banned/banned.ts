import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../../admin/admin.component';
import {ToastService} from '../../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {BannedIP} from '../banned.model';
import {BannedipsRestService} from '../../bannedips-rest.service';
import {rowSelection} from '../../aggrid.utils';
import {AgGridAngular} from 'ag-grid-angular';

export interface BannedIPData {
  address: string;
  IP: string;
  name: string;
  deputy: string;
  date: string;
  reason: string;
  days: number | null;
}

@Component({
  selector: 'app-banned',
  imports: [
    AgGridAngular,
    FormField
  ],
  templateUrl: './banned.html',
  styleUrl: './banned.css',
})
export class Banned extends AdminComponent<BannedIP, string> {
  override restService = inject(BannedipsRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "address", filter: true, width: 150},
    {field: "IP", filter: true, width: 100},
    {field: "name", filter: true, width: 100},
    {field: "deputy", filter: true, width: 100},
    {field: "date", filter: true, width: 100},
  ];

  bannedIPModel = signal<BannedIPData>({
    address: "",
    IP: "",
    days: null,
    name: "",
    deputy: "",
    reason: "",
    date: "",
  });

  form = form(this.bannedIPModel);

  override setForm(): void {
    const bannedIP = this.item();
    this.bannedIPModel.set({
      reason: bannedIP.reason ?? "",
      name: bannedIP.name ?? "",
      IP: bannedIP.IP ?? "",
      address: bannedIP.address ?? "",
      days: bannedIP.days ?? null,
      date: bannedIP.date ?? "",
      deputy: bannedIP.deputy ?? ""
    });
  }

  override makeItem(): BannedIP {
    return new BannedIP();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].address);
  }

  override getForm(): BannedIP {
    const bannedIP = this.item();
    const formModel = this.bannedIPModel();
    bannedIP.reason = formModel.reason == "" ? null : formModel.reason;
    bannedIP.name = formModel.name == "" ? null : formModel.name;
    bannedIP.IP = formModel.IP;
    bannedIP.address = formModel.address == "" ? null : formModel.address;
    bannedIP.days = formModel.days;
    bannedIP.date = formModel.date == "" ? null : formModel.date;
    bannedIP.deputy = formModel.deputy == "" ? null : formModel.deputy;
    return bannedIP;
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
