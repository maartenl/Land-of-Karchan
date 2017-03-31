import { Component, OnInit } from '@angular/core';
import { Player } from './player.model';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  player: Player;
  
  constructor() {
    this.player = new Player();
    this.player.name = 'Karn';
    this.player.title = 'Ruler of Karchan, Keeper of the Key to the Room of Lost Souls';
    this.player.homepageUrl = 'http://www.karchan.org';
    this.player.imageUrl = 'http://www.karchan.org/favico.ico';
    this.player.date_of_birth = 'Sometime';
    this.player.city_of_birth = 'The Dark';
  }

  ngOnInit() {
  }

}
