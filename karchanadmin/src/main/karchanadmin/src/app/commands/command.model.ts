import { AdminObject } from '../admin/admin-object.model';

export class Command implements AdminObject<number> {
    id: number | null = null;
    callable: boolean | null = null;
    command: string | null = null;
    methodName: string | null = null;
    room: number | null = null;
    creation: string | null = null;
    owner: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.id = object.id;
        this.callable = object.callable;
        this.command = object.command;
        this.methodName = object.methodName;
        this.room = object.room;
        this.creation = object.creation;
        this.owner = object.owner;
    }

    getIdentifier(): number | null {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'Command';
    }

}