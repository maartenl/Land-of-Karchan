import { Component, OnInit } from '@angular/core';

import { PlayerService } from '../player.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {
  constructor(private playerService: PlayerService) { }

  hasNewMail: boolean;

  error = false;

  ngOnInit() {
    this.playerService.hasNewMail()
      .subscribe((result: boolean) => { // on success
        this.hasNewMail = result;
      },
        (err: any) => { // error
          this.error = true;
        });
  }

  public playGame(): void {
    this.playerService.enterGame()
      .subscribe(
        (result: any) => { // on success
          if (window.location !== window.parent.location) {
            window.parent.location.href = '/game/play.html';
          }
          window.location.href = '/game/play.html';
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }

  public isLoggedIn(): boolean {
    if (this.error) {
      return false;
    }
    return this.playerService.isLoggedIn();
  }

  public getPlayer(): string {
    return this.playerService.getName();
  }

}
