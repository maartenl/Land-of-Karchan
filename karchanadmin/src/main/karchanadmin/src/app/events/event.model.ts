import { AdminObject } from '../admin/admin-object.model';

export class MudEvent implements AdminObject<number> {
    eventid: number;
    name: string;
    month: number;
    dayofmonth: number;
    hour: number;
    minute: number;
    dayofweek: number;
    callable: boolean;
    methodname: string;
    room: number;
    owner: string;
    creation: string;

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

    getIdentifier(): number {
        return this.eventid;
    }

    setIdentifier(eventid: number): void {
        this.eventid = eventid;
    }

    getType(): string {
        return 'Event';
    }

}