import {Component, inject, signal} from '@angular/core';
import {FormField, form} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {ToastService} from '../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {rowSelection} from '../aggrid.utils';
import {BoardsRestService} from '../boards-rest.service';
import {LinkRenderer} from '../link-renderer/link-renderer';
import {Board, BoardMessage} from './board.model';
import {AgGridAngular} from 'ag-grid-angular';

export interface BoardData {
  id: number | null;
  name: string;
  description: string;
  room: number | null;
  owner: string;
}

@Component({
  selector: 'app-boards',
  imports: [AgGridAngular, FormField],
  templateUrl: './boards.html',
  styleUrl: './boards.css',
})
export class Boards extends AdminComponent<Board, number> {
  override restService = inject(BoardsRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "id", filter: true, width: 150},
    {field: "name", filter: true, width: 150},
    {field: "room", filter: true, width: 150, cellRenderer: LinkRenderer, cellRendererParams: {prefix: "/rooms"}},
    {field: "owner", filter: true, width: 150},
    {field: "creation", width: 150},
  ];

  boardmessage = signal<BoardMessage>(new BoardMessage());

  boardmessages = signal<BoardMessage[]>([]);

  boardModel = signal<BoardData>({
    description: "",
    id: null,
    name: "",
    owner: "",
    room: null
  });

  form = form(this.boardModel);

  override setForm(): void {
    const board = this.item();
    if (board.id !== null) {
      this.restService.getMessages(board.id).subscribe({
        next: (data: BoardMessage[]) => {
          if (data !== undefined) { this.boardmessages.set(data.map(x => new BoardMessage(x))); }
        }
      });
    }
    this.boardModel.set({
      id: board.id ?? null,
      name: board.name ?? "",
      description: board.description ?? "",
      owner: board.owner ?? "",
      room: board.room ?? null,
    });
  }

  override makeItem(): Board {
    return new Board();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].id);
  }

  override getForm(): Board {
    const board = this.item();
    const formModel = this.boardModel();
    board.id = formModel.id;
    board.name = formModel.name == "" ? null : formModel.name;
    board.description = formModel.description == "" ? null : formModel.description;
    board.owner = formModel.owner == "" ? null : formModel.owner;
    board.room = formModel.room;
    return board;
  }

  override setItemById(id: number | null | undefined): boolean {
    if (id === undefined || id === null) {
      return false;
    }
    this.restService.get(id).subscribe({
      next: (data) => {
        if (data !== undefined) {
          this.item.set(data);
          this.setForm();
        }
      }
    });
    return false;
  }

  protected readonly rowSelection = rowSelection;

  protected setBoardmessage(boardmessage: BoardMessage) {
    this.boardmessage.set(boardmessage);
  }

  protected removeMessage(message: BoardMessage) {
    const newmessage = new BoardMessage(message);
    newmessage.removed = !newmessage.removed;
    this.restService.updateMessage(newmessage).subscribe({
      next: (data) => {
        message.removed = !message.removed;
        this.boardmessages.set([...this.boardmessages()]);
        this.toastService.showMessage(message.getType() + ' ' + message.getIdentifier() + " successfully removed.", "Removed...");
      }
    });
    return false;
  }

  protected offensiveMessage(message: BoardMessage) {
    const newmessage = new BoardMessage(message);
    newmessage.offensive = !newmessage.offensive;
    this.restService.updateMessage(newmessage).subscribe({
      next: (data) => {
        message.offensive = !message.offensive;
        this.boardmessages.set([...this.boardmessages()]);
        this.toastService.showMessage(message.getType() + ' ' + message.getIdentifier() + " successfully offensive.", "Offensive...");
      }
    });
    return false;
  }

  protected pinMessage(message: BoardMessage) {
    const newmessage = new BoardMessage(message);
    newmessage.pinned = !newmessage.pinned;
    this.restService.updateMessage(newmessage).subscribe({
      next: (data) => {
        message.pinned = !message.pinned;
        this.boardmessages.set([...this.boardmessages()]);
        this.toastService.showMessage(message.getType() + ' ' + message.getIdentifier() + " successfully pinned.", "Pinned...");
      }
    });
    return false;
  }
}
