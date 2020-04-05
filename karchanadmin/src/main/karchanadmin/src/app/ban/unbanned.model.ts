import { AdminObject } from '../admin/admin-object.model';

export class Unbannedname implements AdminObject<string> {
    name: string;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
    }

    getIdentifier(): string {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Unbanned';
    }

}