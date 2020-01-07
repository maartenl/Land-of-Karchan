import { AdminObject } from '../admin/admin-object.model';

export class Method implements AdminObject<string> {
    name: string;
    src: string;
    creation: string;
    owner: string;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.src = object.src;
        this.creation = object.creation;
        this.owner = object.owner;
    }

    getIdentifier(): string {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }
}
