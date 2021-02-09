import { AdminObject } from '../admin/admin-object.model';

export class Administrator implements AdminObject<string> {
    name: string | null = null;
    title: string | null = null;
    email: string | null = null;
    creation: string | null = null;
    validuntil: string | null = null;

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