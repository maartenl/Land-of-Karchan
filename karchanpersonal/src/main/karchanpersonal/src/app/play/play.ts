import {Component, inject, OnInit, signal, viewChild} from '@angular/core';
import {Router} from '@angular/router';
import {ChatlogService} from '../chatlog.service';
import {PlayerService} from '../player.service';
import {GameService} from '../game.service';
import {AngularEditorConfig, AngularEditorModule} from '@kolkov/angular-editor';
import {Logonmessages} from './logonmessages/logonmessages';
import {Display, Item, Person, WhoPerson} from './display.model';
import {Player} from '../player-settings/player.model';
import {Logger, LogLevel} from '../consolelog.service';
import {Log} from './log.model';
import {form, FormField} from '@angular/forms/signals';
import {LanguageUtils} from '../language.utils';
import {StringUtils} from '../string.utils';
import {Chatlog} from './chatlog/chatlog';
import {RichTextEditor} from '../rich-text-editor/rich-text-editor';

export interface CommandData {
  command: string,
  htmlContent: string,
}

export interface KarchanData {
  name: string,
  sleep: boolean,
  bigEntry: boolean,
  editor: string,
}

@Component({
  selector: 'app-play',
  imports: [AngularEditorModule, FormField, Chatlog, Logonmessages, RichTextEditor],
  templateUrl: './play.html',
  styleUrl: './play.css',
})
export class Play implements OnInit {
  private gameService = inject(GameService)
  private playerService = inject(PlayerService)
  private chatlogService = inject(ChatlogService)
  private router = inject(Router)

  config: AngularEditorConfig = {
    editable: true,
    spellcheck: true,
    height: '15rem',
    minHeight: '5rem',
    enableToolbar: true,
    showToolbar: true,
    placeholder: 'Enter text here...',
    translate: 'no',
    defaultParagraphSeparator: 'p',
    toolbarPosition: 'top',
  };

  logonmessageComponent = viewChild.required(Logonmessages);

  display = signal(new Display());

  karchan = signal<KarchanData>({
    bigEntry: false,
    editor: "",
    name: "",
    sleep: false
  });

  player: Player | null = null;

  commandData = signal<CommandData>({
    command: "",
    htmlContent: ""
  });

  commandForm = form(this.commandData);

  createForms() {
    this.commandData.set({
      command: "",
      htmlContent: ""
    });
  }

  ngOnInit() {
    this.playGame();
  }

  /**
   * Retrieves the player from the server.
   * Start playing the game for reals.
   * Also checks if player is already playing (if so, initializes the play session already in progress.)
   */
  public playGame(): void {
    if (this.gameService.getIsGaming()) {
      this.playInit();
      this.lookAround();
      return;
    }
    Logger.log("Game starting ...", LogLevel.INFO);
    this.playerService.getPlayer()
      .subscribe({
          next: (result: any) => { // on success
            this.player = result;
            if (this.player?.websockets) {
              this.chatlogService.enableWebsockets();
            } else {
              this.chatlogService.disableWebsockets();
            }
            this.gameService.enterGame()
              .subscribe({
                  next: (result: Log) => { // on success
                    this.chatlogService.setLog(new Log(result));
                    this.gameService.setIsGaming(true);
                    this.playInit();
                    this.chatlogService.open(this.karchan.name);
                    this.lookAround();
                    Logger.log("Game started...", LogLevel.INFO);
                  },
                  error: (err: any) => {
                    Logger.logError('error', err);
                  },
                  complete: () => { // on completion
                  }
                }
              );
          },
          error: (err: any) => {
            Logger.logError('error', err);
          },
          complete: () => { // on completion
          }
        }
      );
  }

  public who(): void {
    this.gameService.getWho()
      .subscribe({
          next: (data: any) => { // on success
            if (data !== undefined) {
              let users: Array<WhoPerson> = data.map((x: any) => new WhoPerson(x));
              const display = new Display()
              display.title = 'Who';
              display.image = '';
              if (users.length === 0) {
                display.body = 'There are no people online at the moment.';
              } else {
                display.body = 'There are ' + users.length + ' players.<br/><br/><br/>';
                display.body += '<ul>';
                users.forEach(user => {
                  display.body += '<li>' + user.name + ', ' + user.title + (user.area === 'Land of Karchan' ? '' : ' in ' + user.area);
                  display.body += (user.sleep !== '' ? ', sleeping ' : ' ');
                  display.body += user.afk;
                  display.body += user.idleTime;
                  display.body += " (logged on " + user.min + " minutes and " + user.sec + " seconds ago.)";
                  display.body += '</li>\r\n';
                });
                display.body += '</ul>\r\n';
              }
              this.writeStuff(display);
            }
          },
          error: (err: any) => {
            Logger.logError('error', err);
          },
          complete: () => { // on completion
          }
        }
      );
  }

  /**
   * Initializes the "karchan" object with information about the current player.
   * Also shows logon message, if required.
   */
  public playInit(): void {
    Logger.log('playInit');
    const name = this.retrieveName();
    this.karchan.update(karchan => {
      karchan.name = name;
      karchan.sleep = false;
      return karchan;
    });
    Logger.log('playInit name=' + name);
    if (this.gameService.getShowLogonmessage()) {
      this.showLogonmessage();
    }
  }

  /**
   * Executes a simple look around command (usefull for right after entering the game).
   */
  public lookAround(): void {
    const command = 'l';
    Logger.log('playInit command=' + command);
    this.processCall(command, true);
  }

  public showLogonmessage(): boolean {
    const logonMessages = this.logonmessageComponent();
    if (logonMessages !== undefined) {
      logonMessages.open();
    }
    return false;
  }

  public reconnect(): boolean {
    this.chatlogService.reconnect();
    return false;
  }

  public quit(): boolean {
    Logger.log('quit', LogLevel.INFO);
    this.gameService.quitGame()
      .subscribe({
          next: (result: any) => { // on success
            this.gameService.setIsGaming(false);
            if (this.chatlogService.isWebsocketsEnabled()) {
              this.chatlogService.clear();
              this.chatlogService.close();
            }
            this.router.navigate(['/']);
          },
          error: (err: any) => {
            Logger.logError('error', err);
          },
          complete: () => { // on completion
          }
        }
      );
    return false;
  }

  public play(event: Event): boolean {
    event.preventDefault();
    const formModel = this.commandData();
    const command = formModel.command;
    const htmlContent = formModel.htmlContent;
    Logger.log('play: big talk "' + this.karchan().bigEntry + '"');
    Logger.log('play: command is "' + command + '"');
    Logger.log('play: editor text is "' + htmlContent + '"');
    if (this.karchan().bigEntry) {
      let totalcommand;
      if (command === undefined || command.trim() === '') {
        // there is no command, hopefully only big talk editor contents
        if (htmlContent === undefined) {
          totalcommand = '';
        } else {
          if (htmlContent.startsWith('<p>')) {
            totalcommand = htmlContent.substring(3);
          } else {
            totalcommand = htmlContent
          }
        }
      } else {
        // a command was entered, perhaps with a big talk component
        totalcommand = htmlContent === undefined || htmlContent === '' ? command : command + ' ' + htmlContent;
      }
      Logger.log('play: bigtalk "' + totalcommand + '"');
      if (totalcommand === 'who') {
        this.who();
        this.createForms();
        return false;
      }
      this.processCall(totalcommand, true);
      return false;
    }
    Logger.log('play: normaltalk "' + command + '"');
    if (command !== undefined && command !== null) {
      if (command === 'who') {
        this.who();
        this.createForms();
        return false;
      }
      this.processCall(command, true);
    }
    return false;
  }

  public retrieveName(): string {
    return this.playerService.getName();
  }

  /**
   * Executes the actual command to be transmitted to the game engine.
   * @param command the command to transmit
   * @param log if the logging should be refreshed.
   */
  public processCall(command: string, log: boolean) {
    Logger.log("processCall command=" + command + " log=" + log);
    if (command === 'clear') {
      this.chatlogService.clear();
    }
    this.gameService.processCommand(command, this.chatlogService.getOffset(), !this.chatlogService.isWebsocketsEnabled())
      .subscribe({
          next: (result: Display) => { // on success
            Logger.logObject(result);
            this.writeStuff(result);
            if (result.log !== undefined && result.log !== null) {
              this.chatlogService.setLog(result.log);
            }
            this.createForms();
          },
          error: (err: any) => {
            Logger.logError('error', err);
          },
          complete: () => { // on completion
          }
        }
      );
  }

  public writeStuff(display: Display): void {
    this.display.set(display);
  }

  public getTitle(): string {
    return this.display().title;
  }

  public goWest(): boolean {
    Logger.log('west');
    this.processCall('go west', false);
    return false;
  }

  public goEast(): boolean {
    Logger.log('east');
    this.processCall('go east', false);
    return false;
  }

  public goNorth(): boolean {
    Logger.log('north');
    this.processCall('go north', false);
    return false;
  }

  public goSouth(): boolean {
    Logger.log('South');
    this.processCall('go south', false);
    return false;
  }

  public goUp(): boolean {
    Logger.log('up');
    this.processCall('go up', false);
    return false;
  }

  public goDown(): boolean {
    Logger.log('down');
    this.processCall('go down', false);
    return false;
  }

  public getVowel(person: Person): string {
    if (LanguageUtils.isVowel(person.race.charAt(0))) {
      return 'An';
    }
    return 'A';
  }

  public getDescription(item: Item): string {
    return LanguageUtils.getDescription(item);
  }

  public lookAtPerson(person: Person): boolean {
    Logger.log('lookAtPerson ' + person);
    this.processCall('look at ' + person.name, true);
    return false;
  }

  public lookAtItem(item: Item): boolean {
    Logger.log('lookAtItem ');
    Logger.logObject(item);

    const adjectives = item.adjectives.replace(/,/g, ' ').split(" ").reduce((x, y) => x.trim() + ' ' + y.trim())
    const description = adjectives + ' ' + item.name;
    this.processCall('look at ' + description, true);
    return false;
  }

  public toggleSleep(): boolean {
    Logger.log('toggleSleep');
    if (this.karchan().sleep) {
      // AWAKEN!!!
      this.karchan.update(karchan => {
        karchan.sleep = false;
        return karchan;
      });
      this.processCall('awaken', true);
    } else {
      // GO TO SLEEP!!
      this.karchan.update(karchan => {
        karchan.sleep = true;
        return karchan;
      });
      this.processCall('sleep', true);
    }
    return false;
  }

  public toggleEntry(): boolean {
    Logger.log('toggleEntry');
    this.karchan.update(karchan => {
      karchan.bigEntry = !karchan.bigEntry;
      return karchan;
    });
    return false;
  }

  public getRoomDescription(body: string): string {
    return StringUtils.getCapitalized(body) ?? '';
  }

  public ping(): boolean {
    this.chatlogService.ping();
    return false;
  }
}
