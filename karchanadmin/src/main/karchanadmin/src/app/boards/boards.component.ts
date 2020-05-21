import { Component, OnInit } from '@angular/core';
import { Board } from './board.model';
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

  constructor(
    private boardsRestService: BoardsRestService,
    private formBuilder: FormBuilder,
    private toastService: ToastService) {
    super();
    this.setForm();
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

    const id = this.item === undefined ? null : this.item.id;
    const creation = this.item === undefined ? null : this.item.creation;
    const saveBoard: Board = new Board({
      id,
      name: formModel.name as string,
      description: formModel.description as string,
      room: formModel.room as number,
      creation,
      owner: formModel.owner as string
    });
    return saveBoard;
  }

  setItemById(id: number) {
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
      name: board.name,
      description: board.description,
      room: board.room,
      owner: board.owner
    });
  }

}
