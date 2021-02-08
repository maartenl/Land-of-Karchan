import { AdminObject } from '../admin/admin-object.model';

export class MudEvent implements AdminObject<number> {
    eventid: number | null = null;
    name: string | null = null;
    month: number | null = null;
    dayofmonth: number | null = null;
    hour: number | null = null;
    minute: number | null = null;
    dayofweek: number | null = null;
    callable: boolean | null = null;
    methodname: string | null = null;
    room: number | null = null;
    owner: string | null = null;
    creation: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.eventid = object.eventid;
        this.name = object.name;
        this.month = object.month;
        this.dayofmonth = object.dayofmonth;
        this.hour = object.hour;
        this.minute = object.minute;
        this.dayofweek = object.dayofweek;
        this.callable = object.callable;
        this.methodname = object.methodname;
        this.room = object.room;
        this.owner = object.owner;
        this.creation = object.creation;
    }

    getIdentifier(): number | null {
        return this.eventid;
    }

    setIdentifier(eventid: number): void {
        this.eventid = eventid;
    }

    getType(): string {
        return 'Event';
    }

}