import { AdminObject } from '../admin/admin-object.model';

export class Manpage implements AdminObject<string> {
    command: string | null = null;
    contents: string | null = null;
    synopsis: string | null = null;
    seealso: string | null = null;
    example1: string | null = null;
    example1a: string | null = null;
    example1b: string | null = null;
    example2: string | null = null;
    example2a: string | null = null;
    example2b: string | null = null;
    example2c: string | null = null;

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

    getIdentifier(): string | null {
        return this.command;
    }

    setIdentifier(area: string): void {
        this.command = area;
    }

    getType(): string {
        return 'Manpage';
    }

}