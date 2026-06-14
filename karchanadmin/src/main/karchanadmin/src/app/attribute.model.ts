import {AdminObject} from './admin/admin-object.model';

/**
 * A model for attributes.
 * Attributes are special characteristics of most of the mud objects in the mud.
 */
export class Attribute implements AdminObject<number> {
  /**
   * Id of the attribute, assigned automatically.
   */
  id: number | null = null;
  /**
   * The name of the attribute.
   */
  name: string | null = null;
  /**
   * The actual value of the attribute.
   */
  value: string | null = null;
  /**
   * Type of the value, normally unused.
   */
  valueType: string | null = null;
  /**
   * What kind of mud object contains this attribute. For example: PERSON, ITEM, ROOM.
   */
  objecttype: string | null = null;
  /**
   * De primary key of the mud object that contains this attribute. For example: Karn.
   * Closely related to the objecttype, obviously.
   */
  objectid: string | null = null;

  constructor(object?: any) {
    if (object === undefined) {
      return;
    }
    this.name = object.name ?? null;
    this.id = object.id ?? null;
    this.value = object.value ?? null;
    this.valueType = object.valueType ?? null;
    this.objecttype = object.objecttype ?? null;
    this.objectid = object.objectid ?? null;
  }

  getIdentifier(): number | null {
    return this.id;
  }

  setIdentifier(id: number): void {
    this.id = id;
  }

  getType(): string {
    return 'Attribute';
  }
}
