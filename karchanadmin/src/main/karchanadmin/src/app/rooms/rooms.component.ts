import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { RoomsRestService } from '../rooms-rest.service';
import { Room } from './room.model';
import { Page } from '../page.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent extends DataSource<Room> implements OnInit {

  rooms: Room[];

  datasource: DataSource<Room>;

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
    super();
    if (window.console) {
      console.log('construcotr roomscomponent');
    }
    this.roomsRestService.getCount().subscribe({next : amount => {
      this.rooms = Array.from<Room>({length: amount});
    }});
    this.datasource = this;
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }

  connect(collectionViewer: CollectionViewer): Observable<Room[]> {
    if (window.console) {
      console.log('connect');
    }
    collectionViewer.viewChange.subscribe(range => {
      if (window.console) {
        console.log('viewChange start=' + range.start + ' end=' + range.end + ' size=' + (range.end - range.start));
      }
      this.roomsRestService.getRooms(range.start, range.end).subscribe({
        next: (data) => {
          this.rooms.splice(range.start, range.end - range.start, ...data);
        }
      });
    });
    if (window.console) {
      console.log('end connect');
    }
    return of(this.rooms);
  }

  disconnect(collectionViewer: CollectionViewer): void {
    if (window.console) {
      console.log('disconnect');
    }
  }

}
