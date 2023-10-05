import {Component, OnInit, ViewChild} from '@angular/core';
import { AngularEditorConfig } from '@kolkov/angular-editor';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';

import {PlayerService} from '../player.service';
import {Player} from '../player-settings/player.model';
import {GameService} from '../game.service';
import {Display, Item, Person, WhoPerson} from './display.model';
import {LanguageUtils} from '../language.utils';
import {StringUtils} from '../string.utils';
import {LogonmessageComponent} from './logonmessage/logonmessage.component';
import {ChatlogService} from '../chatlog.service';
import { Log } from './log.model';
import {Logger} from "../consolelog.service";

/**
 * Actually plays teh game, instead of administration of your player character/settings/mail.
 */
@Component({
  selector: 'app-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.css']
})
export class PlayComponent implements OnInit {

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

  @ViewChild(LogonmessageComponent) logonmessageComponent: LogonmessageComponent | null = null;

  Karchan = class {
    name: string = "";
    sleep: boolean = false;
    bigEntry: boolean = false;
    editor: string = "";
  };

  display: Display = new Display();

  karchan = new this.Karchan();

  player: Player | null = null;

  commandForm: FormGroup;

  constructor(
    private gameService: GameService,
    private playerService: PlayerService,
    private chatlogService: ChatlogService,
    private router: Router,
    private formBuilder: FormBuilder) {
    this.commandForm = this.formBuilder.group({
      command: '',
      htmlContent: '',
    });
  }

  createForms() {
    this.commandForm = this.formBuilder.group({
      command: '',
      htmlContent: '',
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
    this.playerService.getPlayer()
      .subscribe(
        (result: any) => { // on success
          this.player = result;
          if (this.player?.websockets) {
            this.chatlogService.enableWebsockets();
          } else {
            this.chatlogService.disableWebsockets();
          }
          this.gameService.enterGame()
            .subscribe(
              (result: Log) => { // on success
                this.chatlogService.setLog(new Log(result));
                this.gameService.setIsGaming(true);
                this.playInit();
                this.chatlogService.open(this.karchan.name);
                this.lookAround();
              },
              (err: any) => { // error
                // console.log('error', err);
              },
              () => { // on completion
              }
            );
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }

  public who(): void {
    this.gameService.getWho()
      .subscribe(
        (data: any) => { // on success
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
                display.body += user.idleTime;
                display.body += '</li>\r\n';
              });
              display.body += '</ul>\r\n';
            }
            this.writeStuff(display);
          }
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
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
    this.karchan.name = name;
    this.karchan.sleep = false;
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
    if (this.logonmessageComponent !== null) {
      this.logonmessageComponent.open();
    }
    return false;
  }

  public reconnect(): boolean {
    this.chatlogService.reconnect();
    return false;
  }

  public quit(): boolean {
    Logger.log('quit');
    this.gameService.quitGame()
      .subscribe(
        (result: any) => { // on success
          this.gameService.setIsGaming(false);
          if (this.chatlogService.isWebsocketsEnabled()) {
            this.chatlogService.clear();
            this.chatlogService.close();
          }
          this.router.navigate(['/']);
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
    return false;
  }

  public play(): boolean {
    const formModel = this.commandForm === null ? null : this.commandForm.value;
    const command = formModel === null ? undefined : formModel.command as string;
    const htmlContent = formModel === null ? undefined : formModel.htmlContent as string;
    Logger.log('play: big talk "' + this.karchan.bigEntry + '"');
    Logger.log('play: command is "' + command + '"');
    Logger.log('play: editor text is "' + htmlContent + '"');
    if (this.karchan.bigEntry) {
      let totalcommand;
      if (command === undefined || command.trim() === '') {
        // there is no command, hopefully only big talk editor contents
        if (htmlContent === undefined) {
          totalcommand = '';
        } else {
          if (htmlContent.startsWith('<p>')) {
            totalcommand = htmlContent.substr(3);
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
      .subscribe(
        (result: Display) => { // on success
          Logger.logObject(result);
          this.writeStuff(result);
          if (result.log !== undefined && result.log !== null) {
            this.chatlogService.setLog(result.log);
          }
          this.createForms();
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }

  public writeStuff(display: Display): void {
    this.display.set(display);
  }

  public getTitle(): string {
    return this.display.title;
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
    if (LanguageUtils.isVowel(person.race.charAt(0))) { return 'An'; }
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
    if (this.karchan.sleep) {
      // AWAKEN!!!
      this.karchan.sleep = false;
      this.processCall('awaken', true);
    } else {
      // GO TO SLEEP!!
      this.karchan.sleep = true;
      this.processCall('sleep', true);
    }
    return false;
  }

  public toggleEntry(): boolean {
    Logger.log('toggleEntry');
    this.karchan.bigEntry = !this.karchan.bigEntry;
    return false;
  }

  public getRoomDescription(body: string): string {
    return StringUtils.getCapitalized(body);
  }

  public ping(): boolean {
    this.chatlogService.ping();
    return false;
  }
}
