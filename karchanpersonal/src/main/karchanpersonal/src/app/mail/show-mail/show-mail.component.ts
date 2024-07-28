import {Component, Input, OnInit} from '@angular/core';
import {Mail} from '../mail.model';
import {FormBuilder, FormGroup} from '@angular/forms';
import {PlayerService} from 'src/app/player.service';
import {ToastService} from 'src/app/toast.service';

@Component({
  selector: 'app-show-mail',
  templateUrl: './show-mail.component.html',
  styleUrls: ['./show-mail.component.css']
})
export class ShowMailComponent implements OnInit {

  @Input() mail: Mail | null = null;

  maildocumentForm: FormGroup;

  constructor(
    private playerService: PlayerService,
    private toastService: ToastService,
    private formBuilder: FormBuilder,
  ) {
    this.maildocumentForm = this.formBuilder.group({
      item_id: 0,
    });
  }

  ngOnInit() {
  }

  createItemFromMail() {
    if (this.mail === null) {
      return;
    }
    const formModel = this.maildocumentForm.value;
    const item_id: number = formModel.item_id as number;

    this.playerService.createItemFromMail(this.mail, item_id).subscribe({
        next: (result: any) => { // on success
          this.toastService.show('Item created from mail.', {
            delay: 3000,
            autohide: true,
            headertext: 'Success...'
          });
        }
      }
    );

  }
}
