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

export class BoardMessage implements AdminObject<number> {
    id: number;
    boardid: number;
    name: string;
    posttime: string;
    message: string;
    removed: boolean;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.id = object.id;
        this.boardid = object.boardid;
        this.name = object.name;
        this.posttime = object.posttime;
        this.message = object.message;
        this.removed = object.removed;
    }

    getIdentifier(): number {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'BoardMessage';
    }

}
