import { Component, OnInit } from '@angular/core';
import { RoomsRestService } from '../rooms-rest.service';
import { Room } from './room.model';
import { Page } from '../page.model';
import { GridOptions, IDatasource, IGetRowsParams, ColDef } from 'ag-grid-community';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent implements OnInit {

  public gridOptions: any;

  public datasource: any;

  columnDefs = [
    { headerName: 'Id', field: 'id' },
    { headerName: 'Title', field: 'title' },
    { headerName: 'North', field: 'north' },
    { headerName: 'South', field: 'south' },
    { headerName: 'West', field: 'west' },
    { headerName: 'East', field: 'east' },
    { headerName: 'Up', field: 'up' },
    { headerName: 'Down', field: 'down' }
  ];

  constructor(private roomsRestService: RoomsRestService) {
    if (window.console) {
      console.log('construcotr roomscomponent');
    }
    this.datasource = {
      getRows: (params: IGetRowsParams) => {
        this.roomsRestService.getRooms(params.startRow, params.endRow)
        .subscribe(data => params.successCallback(data));
      }
    };
    this.gridOptions = {
      cacheBlockSize: 100,
      enableServerSideFilter: false,
      enableServerSideSorting: false,
      rowModelType: 'infinite',
      datasource: this.datasource
    };
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }

}
