import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {Systemlog} from './systemlog.model';
import {SystemlogsRestService} from '../systemlogs-rest.service';
import {ToastService} from '../toast.service';
import {ColDef, SelectionChangedEvent, themeBalham} from 'ag-grid-community';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';
import {Logger} from '../consolelog.service';

export interface SystemlogSearch {
  name: string;
  from: string;
  to: string;
}

@Component({
  selector: 'app-systemlogs',
  imports: [
    AgGridAngular,
    FormField
  ],
  templateUrl: './systemlogs.html',
  styleUrl: './systemlogs.css',
})
export class Systemlogs {

  toastService = inject(ToastService);
  restService = inject(SystemlogsRestService);

  rowData = signal<Systemlog[]>([]);

  log = signal<Systemlog>(new Systemlog());

  colDefs: ColDef[] = [
    {field: "id", filter: true, width: 150},
    {field: "name", filter: true, width: 150},
    {field: "message", filter: true, width: 150},
    {field: "creation", filter: true, width: 150},
  ];

  searchModel = signal<SystemlogSearch>({name: "", from: "", to: ""});

  form = form<SystemlogSearch>(this.searchModel);

  onSelectionChanged(event: SelectionChangedEvent): void {
    this.log.set(event.api.getSelectedRows()[0]);
  }

  public search() {
    const formModel = this.searchModel();
    var name = formModel.name;
    var fromdate = formModel.from;
    var todate = formModel.to;
    Logger.log('search ' + name + ' ' + fromdate + ' ' + todate);

    this.restService.getLogs(name, fromdate, todate)
      .subscribe({
          next: (result: Systemlog[]) => {
            if (result !== undefined) {
              this.rowData.set(result);
            }
            Logger.log('search done');
            this.toastService.showRetrieving("Logging messages successfully retrieved.", "Logging...");
          },
          error: (err: any) => {
            Logger.logError('error in getLogs', err);
          },
          complete: () => { // on completion
          }
        }
      );
  }

  protected readonly rowSelection = rowSelection;
  protected readonly themeBalham = themeBalham;
}
