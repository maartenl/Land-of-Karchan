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
  rooms: Room[] = new Array<Room>();

  cache: any = {};

  page = new Page();

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
    this.page.pageNumber = 0;
    this.page.size = 20;
    this.roomsRestService.getAllRooms().subscribe({
      next: (rooms) => {
        console.log(rooms);
        this.rooms = rooms;
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }


  /**
   * Populate the table with new data based on the page number
   * @param page The page to select
   */
  setPage(pageInfo) {
    if (window.console) {
      console.log('entering setPage');
      console.log(pageInfo);
    }
    this.page.pageNumber = pageInfo.offset;
    this.page.size = pageInfo.pageSize;

    // cache results
    // if(this.cache[this.page.pageNumber]) return;

    this.roomsRestService.getRooms(this.page).subscribe(pagedData => {
      this.page = pagedData.page;

      // calc start
      const start = this.page.pageNumber * this.page.size;

      // copy rows
      const rooms = [...this.rooms];

      // insert rows into new position
      rooms.splice(start, 0, ...pagedData.data);

      // set rows to our new rows
      this.rooms = rooms;

      // add flag for results
      this.cache[this.page.pageNumber] = true;
    });
  }
}
