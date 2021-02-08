import { AdminObject } from '../admin/admin-object.model';

export class Bannedname implements AdminObject<string> {
    name: string | null = null;
    deputy: string | null = null;
    creation: string | null = null;
    days: number | null = null;
    reason: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.days = object.days;
        this.deputy = object.deputy;
        this.creation = object.creation;
        this.reason = object.reason;
    }

    getIdentifier(): string | null {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Bannedname';
    }

}