import { Component, OnInit } from '@angular/core';

import { PlayerService } from '../player.service';
import { HasNewMail } from './newmail.model';
import { ChristmasUtils } from '../christmas.utils';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.css']
})
export class GameComponent implements OnInit {
  constructor(
    private playerService: PlayerService,
    private toastService: ToastService) { }

  hasNewMail: boolean = false;

  error = false;

  ngOnInit() {
    this.playerService.hasNewMail()
      .subscribe((result: HasNewMail) => { // on success
        this.hasNewMail = result.hasMail;
        if (this.hasNewMail) {
          this.toastService.show('You have new mail.', {
            delay: 5000,
            autohide: true,
            headertext: 'Message'
          });
        }
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
