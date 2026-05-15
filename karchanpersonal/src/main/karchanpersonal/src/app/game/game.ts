import {Component, inject, OnInit} from '@angular/core';
import {RouterLink} from '@angular/router';
import {PlayerService} from '../player.service';
import {ToastService} from '../toast.service';
import {HasNewMail} from './newmail.model';
import {ChristmasUtils} from '../christmas.utils';

@Component({
  selector: 'app-game',
  imports: [RouterLink],
  templateUrl: './game.html',
  styleUrl: './game.css',
})
export class Game implements OnInit {
  private playerService: PlayerService = inject(PlayerService)
  private toastService: ToastService = inject(ToastService)

  hasNewMail: boolean = false;

  error = false;

  ngOnInit() {
    this.playerService.hasNewMail()
      .subscribe({
        next: (result: HasNewMail) => { // on success
          this.hasNewMail = result.hasMail;
          if (this.hasNewMail) {
            this.toastService.showMessage("You have new mail.","Message");
          }
        },
        error: (err: any) => { // error
          this.error = true;
        }
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
