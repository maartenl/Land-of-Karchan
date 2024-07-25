import {AdminObject} from '../admin/admin-object.model';

export class ItemDefinition implements AdminObject<number> {
  id: number | null = null;
  name: string | null = null;
  adjectives: string | null = null;
  manaincrease: number | null = null;
  hitincrease: number | null = null;
  vitalincrease: number | null = null;
  movementincrease: number | null = null;
  pasdefense: number | null = null;
  damageresistance: number | null = null;
  eatable: string | null = null;
  drinkable: string | null = null;
  room: number | null = null;
  lightable: number | null = null;
  getable: boolean | null = null;
  dropable: boolean | null = null;
  visible: boolean | null = null;
  wieldable: boolean | null = null;
  description: string | null = null;
  readdescr: string | null = null;
  wearable: number | null = null;
  copper: number | null = null;
  weight: number | null = null;
  container: number | null = null;
  capacity: number | null = null;
  isopenable: boolean | null = null;
  keyid: number | null = null;
  containtype: number | null = null;
  notes: string | null = null;
  image: string | null = null;
  title: string | null = null;
  discriminator: number | null = null;
  bound: boolean | null = null;

  creation: string | null = null;
    owner: string | null = null;

    constructor(object?: any) {
      if (object === undefined) {
        return;
      }
      this.id = object.id;
      this.name = object.name;
      this.adjectives = object.adjectives;
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

    getIdentifier(): number | null {
        return this.id;
    }

    setIdentifier(id: number): void {
        this.id = id;
    }

    getType(): string {
        return 'Item';
    }

}


