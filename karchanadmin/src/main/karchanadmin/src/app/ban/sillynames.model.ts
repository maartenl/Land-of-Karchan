import { AdminObject } from '../admin/admin-object.model';

export class Sillyname implements AdminObject<string> {
    name: string;
    deputy: string;
    creation: string;
    days: number;
    reason: string;

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

    getIdentifier(): string {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Bannedname';
    }

}