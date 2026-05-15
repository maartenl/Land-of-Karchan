import {Component, inject} from '@angular/core';
import {GameService} from '../../game.service';
import {ChatlogService, Message} from '../../chatlog.service';
import {Logger} from '../../consolelog.service';
import {Log} from '../log.model';

@Component({
  selector: 'app-chatlog',
  imports: [],
  templateUrl: './chatlog.html',
  styleUrl: './chatlog.css',
})
export class Chatlog {
  private gameService= inject(GameService)
  private chatlogService = inject(ChatlogService)

  public clearLog(): boolean {
    Logger.log('clearLog');
    this.chatlogService.clear();
    return false;
  }

  public resetLog(): boolean {
    Logger.log('resetLog');
    this.gameService.getLog()
      .subscribe({
          next: (result: Log) => { // on success
            this.chatlogService.clear();
            this.chatlogService.setLog(new Log(result));
            Logger.log('log rest:' + result);
          },
          error: (err: any) => { // error
            Logger.logError('error in resetLog', err);
          },
          complete: () => { // on completion
          }
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
