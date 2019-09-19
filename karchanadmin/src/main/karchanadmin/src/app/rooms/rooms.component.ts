import { Component, OnInit } from '@angular/core';
import { RoomsRestService } from '../rooms-rest.service';
import { Room } from './room.model';
import { Page } from '../page.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent implements OnInit {

  rooms: Room[];

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
    this.roomsRestService.getAllRooms().subscribe({
      next: (data) => {
        this.rooms = data;
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }

}
