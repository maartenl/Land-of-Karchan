import { AdminObject } from '../admin/admin-object.model';

export class Room implements AdminObject<number> {
    id: number;
    west: number;
    east: number;
    north: number;
    south: number;
    up: number;
    down: number;
    contents: string;
    owner: string;
    creation: string;
    area: string;
    title: string;
    picture: string;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.id = object.id;
        this.west = object.west;
        this.east = object.east;
        this.north = object.north;
        this.south = object.south;
        this.up = object.up;
        this.down = object.down;
        this.contents = object.contents;
        this.owner = object.owner;
        this.creation = object.creation;
        this.area = object.area;
        this.title = object.title;
        this.picture = object.picture;
    }

    public getIdentifier(): number {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }
}
