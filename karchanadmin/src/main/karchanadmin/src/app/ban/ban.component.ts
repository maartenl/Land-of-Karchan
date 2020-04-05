import { Component, OnInit } from '@angular/core';

/**
 * Basically an empty component, that delegates to the four different components for administrating banishment stuff.
 */
@Component({
  selector: 'app-ban',
  templateUrl: './ban.component.html',
  styleUrls: ['./ban.component.css']
})
export class BanComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
