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
    this.playerService.getPlayer()
      .subscribe(
      (result: any) => { // on success
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
    this.playerService.getPlayer()
      .subscribe(
      (result: any) => { // on success
        game.playerService.clearName();
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
