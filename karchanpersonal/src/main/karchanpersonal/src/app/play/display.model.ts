
export class Person {
    name: string = '';
    race: string = '';
}

export class Item {
    adject1: string = '';
    adject2: string = '';
    adject3: string = '';
    amount: number = 1;
    name: string = '';
}

export class Log {
    log: string;
    offset: number;
    size: number;

    constructor() {
        this.offset = 0;
        this.log = '';
        this.size = 0;
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
    }

}

