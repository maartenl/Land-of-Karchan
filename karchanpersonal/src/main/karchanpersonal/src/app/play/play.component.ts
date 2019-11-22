import { Component, OnInit } from '@angular/core';
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

  Karchan = class {
    name: string;
    logOffset: number;
    sleep: boolean;
  };

  display: Display = new Display();

  private karchan = new this.Karchan();

  constructor(
    private gameService: GameService,
    private playerService: PlayerService) { }

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

  public retrieveName(): string {
    return this.playerService.getName();
  }

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
    return false;
  }

  public goEast(): boolean {
    if (window.console) { console.log('east'); }
    return false;
  }

  public goNorth(): boolean {
    if (window.console) { console.log('north'); }
    return false;
  }

  public goSouth(): boolean {
    if (window.console) { console.log('South'); }
    return false;
  }

  public goUp(): boolean {
    if (window.console) { console.log('up'); }
    return false;
  }

  public goDown(): boolean {
    if (window.console) { console.log('down'); }
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
    // TODO MLE DO STUFF!
    return false;
  }

  public lookAtItem(item: Item): boolean {
    if (window.console) { console.log('lookAtItem ' + item); }
    // TODO MLE DO STUFF!
    return false;
  }

  public toggleSleep(): boolean {
    if (window.console) { console.log('toggleSleep'); }
    // TODO MLE DO STUFF!
    return false;
  }

  public toggleEntry(): boolean {
    if (window.console) { console.log('toggleEntry'); }
    // TODO MLE DO STUFF!
    return false;
  }

  public clearLog(): boolean {
    if (window.console) { console.log('clearLog'); }
    // TODO MLE DO STUFF!
    return false;
  }

  public quit(): boolean {
    if (window.console) { console.log('quit'); }
    // TODO MLE DO STUFF!
    return false;
  }
}
