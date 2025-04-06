import { AdminObject } from '../admin/admin-object.model';

export class Board implements AdminObject<number> {
    id: number | null = null;
    name: string | null = null;
    description: string | null = null;
    room: number | null = null;
    owner: string | null = null;
    creation: Date | null = null;

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

    getIdentifier(): number | null {
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
    id: number | null = null;
    boardid: number | null = null;
    name: string | null = null;
    posttime: string | null = null;
    message: string | null = null;
    removed: boolean | null = null;
    offensive: boolean | null = null;
    pinned: boolean = false;

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
        this.offensive = object.offensive;
        this.pinned = object.pinned;
    }

    getIdentifier(): number| null {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'BoardMessage';
    }

}
