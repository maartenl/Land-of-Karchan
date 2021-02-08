import { AdminObject } from '../admin/admin-object.model';

export class Area implements AdminObject<string> {
    area: string | null = null;
    description: string | null = null;
    shortdesc: number | null = null;
    creation: string | null = null;
    owner: string | null = null;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.area = object.area;
        this.description = object.description;
        this.shortdesc = object.shortdesc;
        this.creation = object.creation;
        this.owner = object.owner;
    }

    getIdentifier(): string | null {
        return this.area;
    }

    setIdentifier(area: string): void {
        this.area = area;
    }

    getType(): string {
        return 'Area';
    }

}