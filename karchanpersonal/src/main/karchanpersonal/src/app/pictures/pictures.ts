import {Component, inject, OnInit, signal} from '@angular/core';
import {Picture} from './picture.model';
import {PlayerService} from '../player.service';
import {ToastService} from '../toast.service';
import {form, FormField} from '@angular/forms/signals';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-pictures',
  imports: [DatePipe, FormField],
  templateUrl: './pictures.html',
  styleUrl: './pictures.css',
})
export class Pictures implements OnInit {

  private playerService = inject(PlayerService)
  private toastService = inject(ToastService)

  pictures = signal(new Array<Picture>(0));

  picture = signal<Picture>({
    content: "",
    createDate: "",
    id: 0,
    length: 0,
    mimeType: "",
    owner: "",
    url: ""
  })

  player = signal('');

  pictureForm = form(this.picture);

  ngOnInit() {
    this.player.set(this.playerService.getName());
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
            this.pictures.set(result);
          }
        }
    });
  }

  save(event: Event) {
    event.preventDefault();
    const newPicture = this.picture();
    newPicture.createDate = null;
    this.playerService.createPicture(newPicture).subscribe({
      complete: () => {
        this.ngOnInit();
        this.toastService.showMessage("Picture successfully added.", "Added...");
      }
    });
  }

  setPicture(picture: Picture) {
    this.playerService.getPicture(picture.id).subscribe({
      next:
        (result: Picture) => { // on success
          this.picture.set(result);
        }
    });
  }

  deletePicture(picture: Picture) {
    this.playerService.deletePicture(picture).subscribe({
      next:
        (result: any) => { // on success
          this.pictures.update(pictures => pictures.filter(pic => pic.id !== picture.id));
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
          if (!content.startsWith('data:')) {
            return;
          }

          this.picture.update(picture => {
            picture.mimeType = content.substring(5).split(';')[0];
            picture.content = content.split(',')[1];
            return picture;
          });
        }
      };
    }
  }
}
