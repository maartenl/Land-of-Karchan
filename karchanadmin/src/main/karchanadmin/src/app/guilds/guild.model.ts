import { AdminObject } from '../admin/admin-object.model';

export class Guild implements AdminObject<string> {
    name: string;
    title: string;
    guilddescription: string;
    guildurl: string;
    bossname: string;
    logonmessage: string;
    imageurl: string;
    colour: string;
    owner: string;
    creation: Date;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.title = object.title;
        this.guilddescription = object.guilddescription;
        this.guildurl = object.guildurl;
        this.bossname = object.bossname;
        this.logonmessage = object.logonmessage;
        this.imageurl = object.imageurl;
        this.colour = object.colour;
        this.owner = object.owner;
        this.creation = object.creation;

    }

    getIdentifier(): string {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Guild';
    }

}
