import {Component, inject, input, signal} from '@angular/core';
import {Mail} from '../mail.model';
import {PlayerService} from '../../player.service';
import {ToastService} from '../../toast.service';
import {form, FormField} from '@angular/forms/signals';
import {DatePipe} from '@angular/common';

export interface MailDocumentData {
  item_id: string;
}

@Component({
  selector: 'app-show-mail',
  imports: [FormField, DatePipe],
  templateUrl: './show-mail.html',
  styleUrl: './show-mail.css',
})
export class ShowMail {

  private playerService = inject(PlayerService);
  private toastService = inject(ToastService);

  mail = signal(new Mail())

  maildocumentModel = signal<MailDocumentData>({item_id: '0'});

  maildocumentForm = form(this.maildocumentModel);

  createItemFromMail(event: Event) {
    event.preventDefault();
    if (this.mail === null) {
      return;
    }
    const formModel = this.maildocumentModel();
    const item_id: number = Number(formModel.item_id);

    this.playerService.createItemFromMail(this.mail(), item_id).subscribe({
        next: (result: any) => { // on success
          this.toastService.showMessage("Item created from mail.", "Success...");
        }
      }
    );
  }

  setMail(mail: Mail): void {
    this.mail.set(mail);
  }

}
