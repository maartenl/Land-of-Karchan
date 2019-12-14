import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { RoomsRestService } from '../rooms-rest.service';
import { Room } from './room.model';
import { Command } from '../commands/command.model';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent implements OnInit {

  rooms: Room[];

  commands: Command[] = [];

  room: Room;

  form: FormGroup;

  SearchTerms = class {
    owner: string;
  };

  searchTerms = new this.SearchTerms();

  updateOwner(value: string) {
    if (value.trim() === '') {
      value = null;
    }
    this.searchTerms.owner = value;
    this.roomsRestService.getRooms().subscribe({
      next: data => {
        this.rooms = data;
      }
    });
  }

  constructor(
    private roomsRestService: RoomsRestService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder) {
    this.createForm();
    this.room = new Room();
    this.roomsRestService.getRooms().subscribe({
      next: data => {
        this.rooms = data;
      }
    });
  }

  ngOnInit() {
    if (window.console) {
      console.log('ngOnInit');
    }
    const id: string = this.route.snapshot.paramMap.get('id');
    if (id === undefined || id === null) {
      return;
    }
    const idNumber: number = Number(id);
    if (isNaN(idNumber)) {
      return;
    }
    this.setRoomById(idNumber);
  }

  createForm() {
    this.form = this.formBuilder.group({
      id: null,
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
      id: null,
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

  isActive(room: Room) {
    if (room === undefined) {
      return '';
    }
    if (!this.isRoomSelected()) {
      return '';
    }
    return (this.room.id === room.id) ? 'table-active' : '';
  }

  setRoomById(id: number) {
    console.log('setroombyid' + id);
    this.roomsRestService.getRoom(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setRoom(data); }
      }
    });
    this.roomsRestService.getCommands(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.commands = data; }
      }
    });
    return false;
  }

  private setRoom(room: Room) {
    this.room = room;
    this.form.reset({
      id: room.id,
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
        this.rooms = [...this.rooms];
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public createRoom(): void {
    if (window.console) {
      console.log('createRoom');
    }
    const room = this.prepareSave();
    this.roomsRestService.createRoom(room).subscribe(
      (result: number) => { // on success
        if (window.console) {
          console.log('create the room ' + room);
        }
        this.rooms.push(room);
        this.rooms = [...this.rooms];
        this.setRoomById(result);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
    return;
  }

  public updateRoom(): void {
    if (window.console) {
      console.log('updateRoom');
    }
    const room = this.prepareSave();
    const index = this.rooms.findIndex(rm => rm !== null && rm.id === room.id);
    this.roomsRestService.updateRoom(room).subscribe(
      (result: any) => { // on success
        if (window.console) {
          console.log('save the room ' + room);
        }
        if (window.console) {
          console.log('saveRoom' + index);
        }
        this.rooms[index] = room;
        this.rooms = [...this.rooms];
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

  isRoomSelected() {
    return this.room !== undefined && this.room !== null;
  }

  refresh() {
    this.roomsRestService.clearCache();
    this.roomsRestService.getRooms().subscribe({
      next: data => {
        this.rooms = data;
      }
    });
  }
}
