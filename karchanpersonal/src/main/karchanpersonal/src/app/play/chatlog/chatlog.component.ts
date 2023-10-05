import {Component} from '@angular/core';
import {Logger} from "../../consolelog.service";
import {ChatlogService, Message} from "../../chatlog.service";
import {Log} from "../log.model";
import {GameService} from "../../game.service";

@Component({
  selector: 'app-chatlog',
  templateUrl: './chatlog.component.html',
  styleUrls: ['./chatlog.component.css']
})
export class ChatlogComponent {

  constructor(
    private gameService: GameService,
    private chatlogService: ChatlogService,
  ) {
  }

  public clearLog(): boolean {
    Logger.log('clearLog');
    this.chatlogService.clearMessages();
    this.chatlogService.clear();
    return false;
  }

  public resetLog(): boolean {
    Logger.log('resetLog');
    this.gameService.getLog()
      .subscribe(
        (result: Log) => { // on success
          this.chatlogService.clear();
          this.chatlogService.setLog(new Log(result));
          console.log('log rest:' + result);
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
    return false;
  }

  /**
   * Get the log as a string. Suitable for display.
   * @returns a simple string containing the log received from the server (not via websockets). If not available, an empty string.
   */
  public getLog(): string {
    const log = this.chatlogService.getLog();
    return log === null || log === undefined ? '' : log.log;
  }

  public getMessages(): Message[] {
    return this.chatlogService.getMessages();
  }
}
