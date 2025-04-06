import { Component, OnInit } from '@angular/core';
import { Board, BoardMessage } from './board.model';
import { FormGroup, FormBuilder } from '@angular/forms';

import { AdminComponent } from '../admin/admin.component';
import { ToastService } from '../toast.service';
import { AdminRestService } from '../admin/admin-rest.service';
import { BoardsRestService } from '../boards-rest.service';

@Component({
  selector: 'app-boards',
  templateUrl: './boards.component.html',
  styleUrls: ['./boards.component.css']
})
export class BoardsComponent extends AdminComponent<Board, number> implements OnInit {
  form: FormGroup;

  boardmessages: BoardMessage[] = new Array<BoardMessage>(0);

  boardmessage: BoardMessage | null = null;

  constructor(
    private boardsRestService: BoardsRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    const object = {
      id: null,
      name: null,
      description: null,
      room: null,
      owner: null
    };
    this.form = this.formBuilder.group(object);
    this.makeItem();
    this.getItems();
  }

  ngOnInit(): void {
  }

  getItems() {
    this.boardsRestService.getAll()
      .subscribe({
        next: data => {
          this.items = data;
        }
      });
  }

  getRestService(): AdminRestService<Board, number> {
    return this.boardsRestService;
  }

  setForm(item?: Board) {
    const object = item === undefined ? {
      id: null,
      name: null,
      description: null,
      room: null,
      owner: null
    } : item;
    if (this.form === undefined) {
      this.form = this.formBuilder.group(object);
    } else {
      this.form.reset(object);
    }
  }

  makeItem(): Board {
    return new Board();
  }

  getToastService(): ToastService {
    return this.toastService;
  }

  getForm(): Board {
    const formModel = this.form.value;

    const id = this.item === undefined || this.item === null ? null : this.item.id;
    const creation = this.item === undefined || this.item === null ? null : this.item.creation;
    const saveBoard: Board = new Board({
      id: formModel.id as number,
      name: formModel.name as string,
      description: formModel.description as string,
      room: formModel.room as number,
      creation,
      owner: formModel.owner as string
    });
    return saveBoard;
  }

  setItemById(id: number | undefined | null) {
    if (id === undefined || id === null) {
      return false;
    }
    this.boardsRestService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) { this.setBoard(data); }
      }
    });
    return false;
  }

  private setBoard(board: Board) {
    this.item = board;
    this.form.reset({
      id: board.id,
      name: board.name,
      description: board.description,
      room: board.room,
      owner: board.owner
    });
    if (board.id !== null) {
      this.boardsRestService.getMessages(board.id).subscribe({
        next: (data: BoardMessage[]) => {
          if (data !== undefined) { this.boardmessages = data.map(x => new BoardMessage(x)); }
        }
      });
    }
  }

  removeMessage(message: BoardMessage) {
    const newmessage = new BoardMessage(message);
    newmessage.removed = !newmessage.removed;
    this.boardsRestService.updateMessage(newmessage).subscribe({
      next: (data) => {
        message.removed = !message.removed;
        this.boardmessages = [...this.boardmessages];
        this.getToastService().show(message.getType() + ' ' + message.getIdentifier() + ' successfully removed.', {
          delay: 3000,
          autohide: true,
          headertext: 'Removed...'
        });
      }
    });
    return false;
  }

  offensiveMessage(message: BoardMessage) {
    const newmessage = new BoardMessage(message);
    newmessage.offensive = !newmessage.offensive;
    this.boardsRestService.updateMessage(newmessage).subscribe({
      next: (data) => {
        message.offensive = !message.offensive;
        this.boardmessages = [...this.boardmessages];
        this.getToastService().show(message.getType() + ' ' + message.getIdentifier() + ' successfully offensive.', {
          delay: 3000,
          autohide: true,
          headertext: 'Offensive...'
        });
      }
    });
    return false;
  }

  pinMessage(message: BoardMessage) {
    const newmessage = new BoardMessage(message);
    newmessage.pinned = !newmessage.pinned;
    this.boardsRestService.updateMessage(newmessage).subscribe({
      next: (data) => {
        message.pinned = !message.pinned;
        this.boardmessages = [...this.boardmessages];
        this.getToastService().show(message.getType() + ' ' + message.getIdentifier() + ' successfully pinned.', {
          delay: 3000,
          autohide: true,
          headertext: 'Pinned...'
        });
      }
    });
    return false;
  }

  setBoardmessage(message: BoardMessage) {
    this.boardmessage = message;
  }

}
