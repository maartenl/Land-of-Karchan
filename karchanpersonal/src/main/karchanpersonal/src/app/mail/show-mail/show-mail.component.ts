import { Component, OnInit, Input } from '@angular/core';
import { Mail } from '../mail.model';

@Component({
  selector: 'app-show-mail',
  templateUrl: './show-mail.component.html',
  styleUrls: ['./show-mail.component.css']
})
export class ShowMailComponent implements OnInit {

  @Input() mail: Mail;

  constructor() { }

  ngOnInit() {
  }

}
