import { Component, OnInit } from '@angular/core';
import { PlayerService } from '../player.service';
import { Picture } from './picture.model';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { environment } from 'src/environments/environment';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-pictures',
  templateUrl: './pictures.component.html',
  styleUrls: ['./pictures.component.css']
})
export class PicturesComponent implements OnInit {

  form: FormGroup;

  pictures: Picture[] = new Array<Picture>(0);

  picture: Picture | null = null;

  player: string = '';

  constructor(
    private playerService: PlayerService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    this.form = this.formBuilder.group({
      url: '',
      content: '',
      mimeType: ''
    });
  }

  ngOnInit() {
    this.player = this.playerService.getName();
    // retrieve all pictures
    this.playerService.getPictures().subscribe({
      next:
        (result: Picture[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach(value => {
              if (value.createDate !== null) {
                value.createDate = value.createDate.replace('[UTC]', '');
              }
            });
            this.pictures = result;
          }
        }
    });
  }

  getWebsite(): string {
    return environment.website;
  }

  createForm() {
    this.form = this.formBuilder.group({
      url: '',
      content: '',
      mimeType: ''
    });
  }

  save() {
    const newPicture = this.prepareSavePicture();
    this.playerService.createPicture(newPicture).subscribe({
      complete: () => {
        this.ngOnInit();
        this.toastService.show('Picture successfully added.', {
          delay: 3000,
          autohide: true,
          headertext: 'Added...'
        });
      }
    });
  }

  prepareSavePicture(): Picture {
    const formModel = this.form.value;

    const mimeType = formModel.mimeType;
    // return new `Picture` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const savePicture: Picture = {
      id: 0,
      createDate: null,
      length: 0,
      mimeType: mimeType === null || mimeType === undefined || mimeType.trim() === '' ? null : mimeType,
      owner: '',
      url: formModel.url,
      content: formModel.content
    };
    return savePicture;
  }

  cancel() {
    this.createForm();
  }

  setPicture(picture: Picture) {
    this.playerService.getPicture(picture.id).subscribe({
      next:
        (result: Picture) => { // on success
          this.picture = result;
        }
    });
  }

  deletePicture(picture: Picture) {
    this.playerService.deletePicture(picture).subscribe({
      next:
        (result: any) => { // on success
          this.pictures = this.pictures.filter(pic => pic.id !== picture.id);
        }
    });
  }

  onFileChange(event: any) {
    const reader = new FileReader();

    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files;
      reader.readAsDataURL(file);
      // data:image/jpeg;base64,/9j/4AAQSkZJRgABAQ
      reader.onload = () => {
        if (reader.result !== null) {
          const content: string = reader.result.toString();
          if (!content.startsWith('data:')) { return; }

          this.form.patchValue({
            mimeType: content.substring(5).split(';')[0],
            content: content.split(',')[1]
          });
        }
      };
    }
  }

}
