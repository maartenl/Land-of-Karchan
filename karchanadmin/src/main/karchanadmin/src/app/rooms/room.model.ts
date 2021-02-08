import { AdminObject } from '../admin/admin-object.model';

export class Room implements AdminObject<number> {
    id: number | null = null;
    west: number | null = null;
    east: number | null = null;
    north: number | null = null;
    south: number | null = null;
    up: number | null = null;
    down: number | null = null;
    contents: string | null = null;
    owner: string | null = null;
    creation: string | null = null;
    area: string | null = null;
    title: string | null = null;
    picture: string | null = null;

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

    public getIdentifier(): number | null {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'Room';
    }
}
