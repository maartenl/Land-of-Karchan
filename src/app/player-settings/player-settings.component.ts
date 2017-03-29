import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-player-settings',
  templateUrl: './player-settings.component.html',
  styleUrls: ['./player-settings.component.css']
})
export class PlayerSettingsComponent implements OnInit {
  name: string;
  title: string;
  homepageUrl : string;
  imageUrl: string;
  date_of_birth: string;
  city_of_birth: string;
  
  constructor() {
    this.name = 'Karn';
    this.title = 'Ruler of Karchan, Keeper of the Key to the Room of Lost Souls';
    this.homepageUrl = 'http://www.karchan.org';
    this.imageUrl = 'http://www.karchan.org/favico.ico';
    this.date_of_birth = 'Sometime';
    this.city_of_birth = 'The Dark';
  }

  ngOnInit() {
  }

}
