
export class Display {
    body: string;
    image: string;
    items: Array<Item>;
    log: Log;
    persons: Array<Person>;
    north: boolean;
    south: boolean;
    up: boolean;
    down: boolean;
    east: boolean;
    west: boolean;
    title: string;
}

export class Person {
    name: string;
    race: string;
}

export class Item {
    adject1: string;
    adject2: string;
    adject3: string;
    amount: number;
    name: string;
}

export class Log {
    log: string;
    offset: number;
    size: number;
}