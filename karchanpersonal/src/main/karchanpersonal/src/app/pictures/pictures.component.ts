import { Component, OnInit } from '@angular/core';
import { PlayerService } from '../player.service';
import { Picture } from './picture.model';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-pictures',
  templateUrl: './pictures.component.html',
  styleUrls: ['./pictures.component.css']
})
export class PicturesComponent implements OnInit {

  form: FormGroup;

  pictures: Picture[];

  picture: Picture;

  constructor(private playerService: PlayerService,
              private formBuilder: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {
    // retrieve all pictures
    this.playerService.getPictures().subscribe(
      (result: Picture[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach(value => {
          value.createDate = value.createDate.replace('[UTC]', '');
          });
          this.pictures = result;
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  createForm() {
    this.form = this.formBuilder.group({
      url: '',
      content: ''
    });
  }

  send() {
  }

  cancel(){}

  setPicture(picture: Picture) {
    this.picture = picture;
  }
}
