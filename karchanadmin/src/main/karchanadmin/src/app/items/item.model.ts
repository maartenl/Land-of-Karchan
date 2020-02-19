import { AdminObject } from '../admin/admin-object.model';

export class Item implements AdminObject<number> {
    id: number;
    name: string;
    adject1: string;
    adject2: string;
    adject3: string;
    manaincrease: number;
    hitincrease: number;
    vitalincrease: number;
    movementincrease: number;
    pasdefense: number;
    damageresistance: number;
    eatable: string;
    drinkable: string;
    room: number;
    lightable: number;
    getable: boolean;
    dropable: boolean;
    visible: boolean;
    wieldable: boolean;
    description: string;
    readdescr: string;
    wearable: number;
    copper: number;
    weight: number;
    container: number;
    capacity: number;
    isopenable: boolean;
    keyid: number;
    containtype: number;
    notes: string;
    image: string;
    title: string;
    discriminator: number;
    bound: boolean;

    creation: string;
    owner: string;

    constructor(object?: any) {
        if (object === undefined) {
            return;
        }
        this.id = object.id;
        this.name = object.name;
        this.adject1 = object.adject1;
        this.adject2 = object.adject2;
        this.adject3 = object.adject3;
        this.manaincrease = object.manaincrease;
        this.hitincrease = object.hitincrease;
        this.vitalincrease = object.vitalincrease;
        this.movementincrease = object.movementincrease;
        this.pasdefense = object.pasdefense;
        this.damageresistance = object.damageresistance;
        this.eatable = object.eatable;
        this.drinkable = object.drinkable;
        this.room = object.room;
        this.lightable = object.lightable;
        this.getable = object.getable;
        this.dropable = object.dropable;
        this.visible = object.visible;
        this.wieldable = object.wieldable;
        this.description = object.description;
        this.readdescr = object.readdescr;
        this.wearable = object.wearable;
        this.copper = object.copper;
        this.weight = object.weight;
        this.container = object.container;
        this.capacity = object.capacity;
        this.isopenable = object.isopenable;
        this.keyid = object.keyid;
        this.containtype = object.containtype;
        this.notes = object.notes;
        this.image = object.image;
        this.title = object.title;
        this.discriminator = object.discriminator;
        this.bound = object.bound;
        this.creation = object.creation;
        this.owner = object.owner;
    }

    getIdentifier(): number {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'Item';
    }

}