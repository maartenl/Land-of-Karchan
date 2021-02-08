import { AdminObject } from '../admin/admin-object.model';

export class Sillyname implements AdminObject<string> {
    name: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
    }

    getIdentifier(): string | null{
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Silly name';
    }

}