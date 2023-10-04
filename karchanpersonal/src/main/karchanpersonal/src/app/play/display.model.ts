import {Log} from './log.model';

export class Person {
  name: string = '';
  familyname: string = '';
  race: string = '';
}

export class Item {
  adjectives: string = '';
  amount: number = 1;
  name: string = '';
}

export class Display {
  body: string = '';
  image: string = '';
  items: Array<Item> = new Array<Item>(0);

  /**
   * This log is the log received from the server.
   * This should only be used when receiving content from a REST call.
   * Use it to set the log in the chatlog.service.
   */
  log: Log | null = null;
  persons: Array<Person> = new Array<Person>(0);
  north: boolean = false;
  south: boolean = false;
  up: boolean = false;
  down: boolean = false;
  east: boolean = false;
  west: boolean = false;
  title: string = '';

  constructor() {
  }

  set(display: Display) {
    this.body = display.body;
    this.image = display.image;
    this.items = display.items;
    this.persons = display.persons;
    this.north = display.north;
    this.north = display.north;
    this.south = display.south;
    this.up = display.up;
    this.down = display.down;
    this.east = display.east;
    this.west = display.west;
    this.title = display.title;
    this.log = display.log;
  }

}

/**
 * Used by the wholist command.
 */
export class WhoPerson {
  name: string = '';
  familyname: string = '';
  title: string = '';
  area: string = '';
  sleep: string = '';
  /**
   * Looks for example like "(23 min, 45 sec idle)"
   */
  idleTime: string = '';

  constructor(object?: any) {
    if (object === undefined) {
      return;
    }
    this.name = object.name;
    this.title = object.title;
    this.familyname = object.familyname;
    this.area = object.area;
    this.sleep = object.sleep;
    this.idleTime = object.idleTime;
  }
}

