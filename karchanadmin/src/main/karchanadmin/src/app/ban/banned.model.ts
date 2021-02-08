import { AdminObject } from '../admin/admin-object.model';

export class BannedIP implements AdminObject<string> {
    address: string | null = null;
    days: number | null = null;
    IP: string | null = null;
    name: string | null = null;
    deputy: string | null = null;
    date: string | null = null;
    reason: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.address = object.address;
        this.days = object.days;
        this.IP = object.IP;
        this.name = object.name;
        this.deputy = object.deputy;
        this.date = object.date;
        this.reason = object.reason;
    }

    getIdentifier(): string | null {
        return this.address;
    }

    setIdentifier(address: string): void {
        this.address = address;
    }

    getType(): string {
        return 'Banned address';
    }

}