import { AdminObject } from '../admin/admin-object.model';

export class Guild implements AdminObject<string> {
    name: string | null = null;
    title: string | null = null;
    guilddescription: string | null = null;
    guildurl: string | null = null;
    bossname: string | null = null;
    logonmessage: string | null = null;
    imageurl: string | null = null;
    colour: string | null = null;
    owner: string | null = null;
    creation: Date | null = null;

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

    getIdentifier(): string | null {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Guild';
    }

}

export class Guildmember implements AdminObject<string> {
    name: string | null = null;
    guildrank: string | null = null;
    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.guildrank = object.guildrank;
    }

    getIdentifier(): string | null {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Guildmember';
    }
}
