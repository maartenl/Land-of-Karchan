
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

export class Log {
  log: string;
  offset: number;
  size: number;
  totalsize: number;

  constructor(object?: any) {
    if (object === undefined) {
      this.offset = 9999999999999;
      this.totalsize = 9999999999999;
      this.log = '';
      this.size = 0;
      return;
    }
    this.offset = object.offset;
    this.log = object.log;
    this.size = object.size;
    this.totalsize = object.totalsize;
  }

}

export class Display {
  body: string = '';
  image: string = '';
  items: Array<Item> = new Array<Item>(0);
  log: Log;
  persons: Array<Person> = new Array<Person>(0);
  north: boolean = false;
  south: boolean = false;
  up: boolean = false;
  down: boolean = false;
  east: boolean = false;
  west: boolean = false;
  title: string = '';

  constructor() {
    this.log = new Log();
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
    if (display.log !== null && display.log !== undefined) {
      this.log = display.log;
    }
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

