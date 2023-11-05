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
  /**
   * Minutes when last logged in. For instance: logged on 23 minutes and 20 seconds ago. (see {@link sec}.
   */
  min: number = 0;
  /**
   * Seconds when last logged in. For instance: logged on 23 minutes and 10 seconds ago. (see {@link min}.
   */
  sec: number = 0;

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
    this.min = object.min;
    this.sec = object.sec;
  }
}

