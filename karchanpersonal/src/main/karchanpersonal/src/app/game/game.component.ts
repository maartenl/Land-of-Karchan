import { Component, OnInit } from '@angular/core';

import { PlayerService } from '../player.service';
import { HasNewMail } from './newmail.model';
import { ChristmasUtils } from '../christmas.utils';

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
      .subscribe((result: HasNewMail) => { // on success
        this.hasNewMail = result.hasMail;
      },
        (err: any) => { // error
          this.error = true;
        });
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

  public isChristmas(): boolean {
    return ChristmasUtils.isChristmas();
  }

}
