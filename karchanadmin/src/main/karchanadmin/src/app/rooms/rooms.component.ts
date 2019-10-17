import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

import { RoomsRestService } from '../rooms-rest.service';
import { Room } from './room.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent extends DataSource<Room> implements OnInit {

  rooms: Room[];

  room: Room;

  form: FormGroup;

  datasource: DataSource<Room>;

  private dataStream;

  constructor(
    private roomsRestService: RoomsRestService,
    private formBuilder: FormBuilder) {
    super();
    this.createForm();
    this.room = new Room();
    this.roomsRestService.getCount().subscribe({
      next: amount => {
        this.rooms = Array.from<Room>({ length: amount });
        this.dataStream = new BehaviorSubject<(Room | undefined)[]>(this.rooms);
      }
    });
    this.datasource = this;
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
  }

  createForm() {
    this.form = this.formBuilder.group({
      title: '',
      picture: null,
      contents: null,
      north: null,
      south: null,
      west: null,
      east: null,
      up: null,
      down: null,
      owner: null,
      area: null
    });
  }

  resetForm() {
    this.form.reset({
      title: '',
      picture: null,
      contents: null,
      north: null,
      south: null,
      west: null,
      east: null,
      up: null,
      down: null,
      owner: null,
      area: null
    });
  }

  public cancel(): void {
    this.resetForm();
    this.room = new Room();
  }

  connect(collectionViewer: CollectionViewer): Observable<Room[]> {
    if (window.console) {
      console.log('connect');
    }
    // this.subscription.add(
    collectionViewer.viewChange.subscribe(range => {
      if (this.rooms.slice(range.start, range.end).some(x => x === undefined)) {
        if (window.console) {
          console.log('Call restservice');
        }
        this.roomsRestService.getRooms(range.start, range.end + 100).subscribe({
          next: (data) => {
            this.rooms.splice(range.start, data.length, ...data);
            this.rooms = [...this.rooms];
            this.dataStream.next(this.rooms);
          }
        });
      }
    });
    return this.dataStream;
  }

  disconnect(collectionViewer: CollectionViewer): void {
    if (window.console) {
      console.log('disconnect');
    }
    // this.subscription.unsubscribe();
  }

  isActive(room: Room) {
    if (this.room === undefined) {
      return '';
    }
    if (room === undefined) {
      return '';
    }
    return (this.room.id === room.id) ? 'table-active' : '';
  }

  setRoom(room: Room) {
    this.room = room;
    this.form.reset({
      title: room.title,
      picture: room.picture,
      contents: room.contents,
      north: room.north,
      south: room.south,
      west: room.west,
      east: room.east,
      up: room.up,
      down: room.down,
      owner: room.owner,
      area: room.area
    });
  }

  public deleteRoom(): void {
    if (window.console) {
      console.log('deleteRoom ' + this.room.id);
    }
    this.roomsRestService.deleteRoom(this.room).subscribe(
      (result: any) => { // on success
        this.rooms = this.rooms.filter((bl) => bl === undefined || bl.id !== this.room.id);
        // this.rooms.push(undefined);
        this.rooms = [...this.rooms];
        this.dataStream.next(this.rooms);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public saveRoom(): void {
    if (window.console) {
      console.log('saveRoom');
    }
    const index = this.rooms.indexOf(this.room);
    if (window.console) {
      console.log('saveRoom' + index);
    }
    const room = this.prepareSave();
    this.roomsRestService.updateRoom(room).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('save the room ' + room);
        }
        if (room.id !== undefined) {
          this.rooms[index] = room;
        }
        this.rooms = [...this.rooms];
        this.dataStream.next(this.rooms);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  prepareSave(): Room {
    const formModel = this.form.value;

    // return new `Room` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const saveRoom: Room = {
      id: this.room.id as number,
      title: formModel.title as string,
      contents: formModel.contents as string,
      area: formModel.area as string,
      picture: formModel.picture as string,
      north: formModel.north as number,
      south: formModel.south as number,
      west: formModel.west as number,
      east: formModel.east as number,
      up: formModel.up as number,
      down: formModel.down as number,
      creation: this.room.creation as string,
      owner: this.room.owner as string
    };
    return saveRoom;
  }

  disownRoom() {
    if (this.room === undefined || this.room === null) {
      return;
    }
    this.room.owner = null;
    this.dataStream.next(this.rooms);
}
}
