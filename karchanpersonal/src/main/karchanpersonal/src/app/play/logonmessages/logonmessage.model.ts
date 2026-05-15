import {Board} from "./board.model";

export class Logonmessage {
  guildmessage: string;
  guildAlarmDescription: string;
  colour: string;
  newsBoard: Board;

  constructor(object?: any) {
    if (object === undefined) {
      this.guildmessage = '';
      this.guildAlarmDescription = '';
      this.colour = '#000000';
      this.newsBoard = new Board();
      return;
    }
    this.guildmessage = object.guildmessage;
    this.guildAlarmDescription = object.guildAlarmDescription;
    this.colour = object.colour;
    this.newsBoard = new Board(object.newsBoard);
  }

}
