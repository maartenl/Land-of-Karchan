import {AdminObject} from '../admin/admin-object.model';

export class Blog implements AdminObject<number> {
  id: number | null = null;
  name: string | null = null;
  contents: string | null = null;
  modification: string | null = null;
  title: string | null = null;
  urlTitle: string | null = null;
  creation: string | null = null;

  constructor(object?: any) {
    if (object === undefined) {
      return;
    }
    this.id = object.id;
    this.name = object.name;
    this.contents = object.contents;
    this.modification = object.modification;
    this.title = object.title;
    this.urlTitle = object.titurlTitlele;
    this.creation = object.creation;
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
