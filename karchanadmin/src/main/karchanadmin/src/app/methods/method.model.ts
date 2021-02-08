import { AdminObject } from '../admin/admin-object.model';

export class Method implements AdminObject<string> {
    name: string | null = null;
    src: string | null = null;
    creation: string | null = null;
    owner: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.src = object.src;
        this.creation = object.creation;
        this.owner = object.owner;
    }

    getIdentifier(): string | null {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Method';
    }
}
