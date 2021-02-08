import { Component, ViewChild, OnInit, TemplateRef } from '@angular/core';
import { GameService } from 'src/app/game.service';

import {NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-logonmessage',
  templateUrl: './logonmessage.component.html',
  styleUrls: ['./logonmessage.component.css']
})
export class LogonmessageComponent implements OnInit {

  @ViewChild('content') private content: TemplateRef<any> | null = null;

  closeResult = '';

  logonmessage: string = '';

  constructor(
    private modalService: NgbModal,
    private gameService: GameService
  ) { }

  ngOnInit(): void {
    this.gameService.getLogonmessage()
    .subscribe(
      (result: string) => { // on success
        this.logonmessage = result;
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
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
}
