/**
 * A model for attributes.
 * Attributes are special characteristics of most of the mud objects in the mud.
 */
export class Attribute {
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
    this.name = object.name;
    this.id = object.id;
    this.value = object.value;
    this.valueType = object.valueType;
    this.objecttype = object.objecttype;
    this.objectid = object.objectid;
  }
}
