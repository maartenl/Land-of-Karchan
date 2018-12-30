import { Component, OnInit } from '@angular/core';

import { PlayerService } from 'app/player.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {

  constructor(private playerService: PlayerService) { }

  hasNewMail: boolean;

  error: boolean = false;

  ngOnInit() {
    this.playerService.hasNewMail()
      .subscribe( (result: boolean) => { // on success
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
        if (window.location != window.parent.location) {
              window.parent.location.href = "/game/play";
        }
        window.location.href = "/game/play";
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
              window.parent.location.href = "/game/goodbye";
        }
        window.location.href = "/game/goodbye";
      },
      (err: any) => { // error
        // console.log("error", err);
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
