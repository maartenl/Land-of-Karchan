import { AdminObject } from '../admin/admin-object.model';

export class Command implements AdminObject<number> {
    id: number;
    callable: boolean;
    command: string;
    methodName: string;
    room: number;
    creation: string;
    owner: string;

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

    getIdentifier(): number {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'Command';
    }

}