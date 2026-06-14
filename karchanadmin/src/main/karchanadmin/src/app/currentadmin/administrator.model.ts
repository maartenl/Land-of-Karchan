import { AdminObject } from '../admin/admin-object.model';

export class Administrator implements AdminObject<string> {
    name: string = "";
    title: string = "";
    email: string = "";
    creation: string = "";
    validuntil: string = "";

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.name = object.name;
        this.title = object.title;
        this.email = object.email;
        this.creation = object.creation;
        this.validuntil = object.validuntil;
    }

    getIdentifier(): string | null {
        return this.name;
    }

    setIdentifier(name: string): void {
        this.name = name;
    }

    getType(): string {
        return 'Administrator';
    }

}
