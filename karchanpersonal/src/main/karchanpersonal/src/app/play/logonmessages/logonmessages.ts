import {Component, inject, OnInit, signal, TemplateRef, ViewChild} from '@angular/core';
import {Logonmessage} from './logonmessage.model';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {GameService} from '../../game.service';
import {Logger} from '../../consolelog.service';

@Component({
  selector: 'app-logonmessages',
  imports: [],
  templateUrl: './logonmessages.html',
  styleUrl: './logonmessages.css',
})
export class Logonmessages implements OnInit {

  private modalService = inject(NgbModal)
  private gameService = inject(GameService)

  @ViewChild('content') private content: TemplateRef<any> | null = null;

  closeResult = '';

  logonmessage = signal(new Logonmessage());

  ngOnInit(): void {
    this.gameService.getLogonmessage()
      .subscribe({
          next: (result) => this.logonmessage.set(new Logonmessage(result))
        }
      );
  }

  close(message: string) {
    this.modalService.dismissAll();
    this.gameService.setShowLogonmessage(false);
  }

  dismiss(message: string) {
    this.modalService.dismissAll();
    this.gameService.setShowLogonmessage(false);
  }

  open() {
    this.modalService.open(this.content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  getColour(): string {
    const style = "chat-" + this.logonmessage().colour;
    Logger.logEntering("getColour " + style);
    return style;
  }
}
