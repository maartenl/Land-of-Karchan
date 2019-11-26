import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';

import { PlayerService } from '../player.service';
import { GameService } from '../game.service';
import { Display, Person, Item } from './display.model';
import { LanguageUtils } from '../language.utils';

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

  Karchan = class {
    name: string;
    logOffset: number;
    sleep: boolean;
    bigEntry: boolean;
  };

  display: Display = new Display();

  private karchan = new this.Karchan();

  constructor(
    private gameService: GameService,
    private playerService: PlayerService,
    private router: Router) {
    this.karchan.bigEntry = false;
    this.karchan.sleep = false;
    this.karchan.logOffset = 0;
  }

  ngOnInit() {
    this.playGame();
  }

  public playGame(): void {
    this.gameService.enterGame()
      .subscribe(
        (result: any) => { // on success
          this.playInit();
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }

  public playInit(): void {
    if (window.console) { console.log('playInit'); }
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

  }

  public quit(): boolean {
    if (window.console) { console.log('quit'); }
    this.gameService.quitGame()
      .subscribe(
        (result: any) => { // on success
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
    this.gameService.processCommand(command, this.karchan.logOffset, true)
      .subscribe(
        (result: any) => { // on success
          this.writeStuff(result);
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }

  public writeStuff(display: Display): void {
    this.display = display;
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
    // TODO MLE DO STUFF!
    return false;
  }

  public clearLog(): boolean {
    if (window.console) { console.log('clearLog'); }
    this.display.log.offset = this.display.log.log.length;
    this.display.log.size = this.display.log.log.length;
    return false;
  }

  public getLog(): string {
    if (this.display.log === undefined) {
      return '';
    }
    return this.display.log.log.substr(this.display.log.offset);
  }

}
