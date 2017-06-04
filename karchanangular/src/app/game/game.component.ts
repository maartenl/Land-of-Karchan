import { Component, OnInit } from '@angular/core';

import { PlayerService } from 'app/player.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {

  constructor(private playerService: PlayerService) { }

  ngOnInit() {
  }

  public playGame(): void {
    this.playerService.enterGame()
      .subscribe(
      (result: any) => { // on success
        if (window.location != window.parent.location) {
              window.parent.location.href = "/web/guest/play";
        }
        window.location.href = "/web/guest/play";
      },
      (err: any) => { // error
        // console.log("error", err);
      },
      () => { // on completion
      }
      );
  }

  public logoff(): void {
    let game = this;
    this.playerService.logoff()
      .subscribe(
      (result: any) => { // on success
        game.playerService.clearName();
        if (window.location != window.parent.location) {
              window.parent.location.href = "/web/guest/goodbye";
        }
        window.location.href = "/web/guest/goodbye";
      },
      (err: any) => { // error
        // console.log("error", err);
      },
      () => { // on completion
      }
      );

  }

  public isLoggedIn(): boolean {
    return this.playerService.isLoggedIn();
  }

  public getPlayer(): string {
    return this.playerService.getName();
  }
}
