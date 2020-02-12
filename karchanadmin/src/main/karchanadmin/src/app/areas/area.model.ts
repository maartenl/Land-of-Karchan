import { AdminObject } from '../admin/admin-object.model';

export class Area implements AdminObject<string> {
    area: string;
    description: string;
    shortdesc: number;
    creation: string;
    owner: string;

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

    getIdentifier(): string {
        return this.area;
    }

    setIdentifier(area: string): void {
        this.area = area;
    }

    getType(): string {
        return 'Area';
    }

}