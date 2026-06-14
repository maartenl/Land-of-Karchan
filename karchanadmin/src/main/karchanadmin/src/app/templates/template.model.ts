import {AdminObject} from '../admin/admin-object.model';

export class Template implements AdminObject<number> {
  id: number | null = null;
  content: string | null = null;
  name: string | null = null;
  version: number | null = null;
  editor: string | null = null;
  comment: string | null = null;
  created: string | null = null;
  modified: string | null = null;

  constructor(object?: any) {
    if (object === undefined) {
      return;
    }
    this.id = object.id;
    this.name = object.name;
    this.content = object.content;
    this.modified = object.modified;
    this.version = object.version;
    this.editor = object.editor;
    this.created = object.created;
    this.comment = object.comment;
  }

  getIdentifier(): number | null {
    return this.id;
  }

  setIdentifier(id: number): void {
    this.id = id;
  }

  getType(): string {
    return 'Blog';
  }
}
