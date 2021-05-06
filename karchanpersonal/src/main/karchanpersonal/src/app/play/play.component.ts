import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import {ChangeEvent} from '@ckeditor/ckeditor5-angular/ckeditor.component';
import {FormBuilder, FormGroup} from '@angular/forms';

import {PlayerService} from '../player.service';
import {GameService} from '../game.service';
import {Display, Item, Log, Person, WhoPerson} from './display.model';
import {LanguageUtils} from '../language.utils';
import {StringUtils} from '../string.utils';
import {LogonmessageComponent} from './logonmessage/logonmessage.component';
import {ChatlogService, Message} from '../chatlog.service';

/**
 * Actually plays teh game, instead of administration of your player character/settings/mail.
 */
@Component({
  selector: 'app-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.css']
})
export class PlayComponent implements OnInit {
  public Editor = ClassicEditor;

  @ViewChild(LogonmessageComponent) logonmessageComponent: LogonmessageComponent | null = null;

  Karchan = class {
    name: string = "";
    logOffset: number = 0;
    sleep: boolean = false;
    bigEntry: boolean = false;
    editor: string = "";
    log: string = "";
  };

  display: Display = new Display();

  karchan = new this.Karchan();

  commandForm: FormGroup;

  constructor(
    private gameService: GameService,
    private playerService: PlayerService,
    private chatlogService: ChatlogService,
    private router: Router,
    private formBuilder: FormBuilder) {
    this.commandForm = this.formBuilder.group({
      command: ''
    });
  }

  createForms() {
    this.commandForm = this.formBuilder.group({
      command: ''
    });
  }

  ngOnInit() {
    this.playGame();
  }

  public playGame(): void {
    if (this.gameService.getIsGaming()) {
      this.playInit();
      return;
    }
    this.gameService.enterGame()
      .subscribe(
        (result: any) => { // on success
          this.gameService.setIsGaming(true);
          this.playInit();
          this.chatlogService.open(this.karchan.name);
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

  public playInit(): void {
    if (window.console) {
      console.log('playInit');
    }
    const name = this.retrieveName();
    this.karchan.name = name;
    this.karchan.logOffset = 0;
    this.karchan.sleep = false;
    if (window.console) {
      console.log('playInit name=' + name);
    }
    const command = 'l';
    if (window.console) {
      console.log('playInit command=' + command);
    }
    this.processCall(command, true);
    if (this.gameService.getShowLogonmessage()) {
      this.showLogonmessage();
    }
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
    if (window.console) { console.log('quit'); }
    this.gameService.quitGame()
      .subscribe(
        (result: any) => { // on success
          this.display.log = new Log();
          this.display.log.log = "";
          this.display.log.size = 0;
          this.display.log.offset = 0;
          this.gameService.setIsGaming(false);
          this.chatlogService.clearMessages();
          this.chatlogService.close();
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

  public onChange({ editor }: ChangeEvent) {
    this.karchan.editor = editor.getData();
  }

  public play(): boolean {
    const formModel = this.commandForm === null ? null : this.commandForm.value;
    const command = formModel === null ? undefined : formModel.command as string;
    if (this.karchan.bigEntry) {
      let totalcommand;
      if (command === undefined || command.trim() === '') {
        // there is no command, hopefully only big talk editor contents
        if (this.karchan.editor === undefined) {
          totalcommand = '';
        } else {
          if (this.karchan.editor.startsWith('<p>')) {
            totalcommand = this.karchan.editor.substr(3);
          } else {
            totalcommand = this.karchan.editor;
          }
        }
      } else {
        // a command was entered, perhaps with a big talk component
        totalcommand = this.karchan.editor === undefined || this.karchan.editor === '' ? command : command + ' ' + this.karchan.editor;
      }
      if (window.console) {
        console.log('play: "' + totalcommand + '"');
      }
      if (totalcommand === 'who') {
        this.who();
        this.createForms();
        return false;
      }
      this.processCall(totalcommand, true);
      return false;
    }
    if (window.console) { console.log('play: "' + command + '"'); }
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
    if (command === 'clear') {
      this.karchan.logOffset = 0;
      log = true;
    }
    this.gameService.processCommand(command)
      .subscribe(
        (result: any) => { // on success
          this.writeStuff(result);
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
    if (window.console) { console.log('west'); }
    this.processCall('go west', false);
    return false;
  }

  public goEast(): boolean {
    if (window.console) { console.log('east'); }
    this.processCall('go east', false);
    return false;
  }

  public goNorth(): boolean {
    if (window.console) { console.log('north'); }
    this.processCall('go north', false);
    return false;
  }

  public goSouth(): boolean {
    if (window.console) { console.log('South'); }
    this.processCall('go south', false);
    return false;
  }

  public goUp(): boolean {
    if (window.console) { console.log('up'); }
    this.processCall('go up', false);
    return false;
  }

  public goDown(): boolean {
    if (window.console) { console.log('down'); }
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
    if (window.console) { console.log('lookAtPerson ' + person); }
    this.processCall('look at ' + person.name, true);
    return false;
  }

  public lookAtItem(item: Item): boolean {
    if (window.console) { console.log('lookAtItem ' + item); }
    const itemarray = [item.adject1, item.adject2, item.adject3, name];
    this.processCall('look at ' + itemarray.reduce((prev, cur) => prev + ' ' + cur) + ' ', true);
    return false;
  }

  public toggleSleep(): boolean {
    if (window.console) { console.log('toggleSleep'); }
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
    if (window.console) { console.log('toggleEntry'); }
    this.karchan.bigEntry = !this.karchan.bigEntry;
    return false;
  }

  public clearLog(): boolean {
    if (window.console) { console.log('clearLog'); }
    const messages = this.chatlogService.getMessages();
    this.chatlogService.clearMessages();
    messages.forEach(message => this.display.log.log = this.display.log.log + message.content);
    this.display.log.offset = this.display.log.log.length;
    this.display.log.size = this.display.log.log.length;
    return false;
  }

  public resetLog(): boolean {
    if (window.console) { console.log('resetLog'); }
    this.gameService.getLog()
      .subscribe(
        (result: string) => { // on success
          this.display.log = new Log();
          this.display.log.log = result;
          this.display.log.size = result.length;
          this.display.log.offset = 0;
          this.chatlogService.clearMessages();
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

  public getLog(): string {
    if (this.display.log === undefined) {
      if (window.console) { console.log('empty log'); }
      return '';
    }
    const log = this.display.log.log.substr(this.display.log.offset);
    if (window.console) { console.log('log ui:' + log); }
    return log;
  }

  public getRoomDescription(body: string): string {
    return StringUtils.getCapitalized(body);
  }

  public getMessages(): Message[] {
    return this.chatlogService.getMessages();
  }

  public ping(): boolean {
    this.chatlogService.ping();
    return false;
  }
}
