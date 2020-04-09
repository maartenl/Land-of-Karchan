import { AdminObject } from '../admin/admin-object.model';

export class BannedIP implements AdminObject<string> {
    address: string;
    days: number;
    IP: string;
    name: string;
    deputy: string;
    date: string;
    reason: string;

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

    getIdentifier(): string {
        return this.address;
    }

    setIdentifier(address: string): void {
        this.address = address;
    }

    getType(): string {
        return 'Banned address';
    }

}