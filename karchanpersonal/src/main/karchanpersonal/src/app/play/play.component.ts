import { Component, OnInit } from '@angular/core';
import { PlayerService } from '../player.service';

/**
 * Actually plays teh game, instead of administration of your player character/settings/mail.
 */
@Component({
  selector: 'app-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.css']
})
export class PlayComponent implements OnInit {

  constructor(private playerService: PlayerService) { }

  ngOnInit() {
    this.playGame();
  }

  public playGame(): void {
    this.playerService.enterGame()
      .subscribe(
        (result: any) => { // on success
          this.setupDisplay();
        },
        (err: any) => { // error
          // console.log('error', err);
        },
        () => { // on completion
        }
      );
  }

  public setupDisplay(): void {

  }
}
