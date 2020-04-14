import { AdminObject } from '../admin/admin-object.model';

export class Worldattribute implements AdminObject<string> {
    name: string;
    contents: string;
    type: string;
    owner: string;
    creation: string;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.contents = object.contents;
        this.type = object.type;
        this.owner = object.owner;
        this.creation = object.creation;
    }

    getIdentifier(): string {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Worldattribute';
    }

}