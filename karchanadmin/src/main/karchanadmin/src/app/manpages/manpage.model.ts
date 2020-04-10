import { AdminObject } from '../admin/admin-object.model';

export class Manpage implements AdminObject<string> {
    command: string;
    contents: string;
    synopsis: string;
    seealso: string;
    example1: string;
    example1a: string;
    example1b: string;
    example2: string;
    example2a: string;
    example2b: string;
    example2c: string;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.command = object.command;
        this.contents = object.contents;
        this.synopsis = object.synopsis;
        this.seealso = object.seealso;
        this.example1 = object.example1;
        this.example1a = object.example1a;
        this.example1b = object.example1b;
        this.example2 = object.example2;
        this.example2a = object.example2a;
        this.example2b = object.example2b;
        this.example2c = object.example2c;
        }

    getIdentifier(): string {
        return this.command;
    }

    setIdentifier(area: string): void {
        this.command = area;
    }

    getType(): string {
        return 'Manpage';
    }

}