import { AdminObject } from '../admin/admin-object.model';

export class Board implements AdminObject<number> {
    id: number;
    name: string;
    description: string;
    room: number;
    owner: string;
    creation: Date;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.id = object.id;
        this.name = object.name;
        this.description = object.description;
        this.room = object.room;
        this.owner = object.owner;
        this.creation = object.creation;

    }

    getIdentifier(): number {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'Board';
    }

}