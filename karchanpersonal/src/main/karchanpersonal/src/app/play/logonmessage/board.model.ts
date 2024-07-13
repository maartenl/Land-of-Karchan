export class Board {
  description: string;
  messages: BoardMessage[];

  constructor(object?: any) {
    if (object === undefined) {
      this.description = '';
      this.messages = [];
      return;
    }
    this.description = object.description;
    this.messages = object.messages.map((x: any) => new BoardMessage(x));
  }

}

export class BoardMessage {
  posttime: Date;
  message: string;
  name: string;

  constructor(object?: any) {
    if (object === undefined) {
      this.posttime = new Date();
      this.message = '';
      this.name = '';
      return;
    }
    this.posttime = object.posttime;
    this.message = object.message;
    this.name = object.name;
  }

}

