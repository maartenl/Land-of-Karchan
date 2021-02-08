import { AdminObject } from '../admin/admin-object.model';

export class Worldattribute implements AdminObject<string> {
    name: string | null;
    contents: string | null;
    type: string | null;
    owner: string | null;
    creation: string | null;

    constructor(object?: any) {
        if (object === undefined) {
            this.name = null;
            this.contents = null;
            this.type = null;
            this.owner = null;
            this.creation = null;
            return;
        }
        this.name = object.name;
        this.contents = object.contents;
        this.type = object.type;
        this.owner = object.owner;
        this.creation = object.creation;
    }

    getIdentifier(): string | null {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Worldattribute';
    }

}