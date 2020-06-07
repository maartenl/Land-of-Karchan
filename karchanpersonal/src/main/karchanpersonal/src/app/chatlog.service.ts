import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { ToastService } from './toast.service';

export class Message {
  from: string;
  to: string;
  content: string;

  constructor() { }

}

@Injectable({
  providedIn: 'root'
})
export class ChatlogService {

  private connectionOpen: boolean;

  private myWebSocket: WebSocketSubject<Message>;

  private messages: Message[] = [];

  constructor(
    private toastService: ToastService
  ) {
  }

  open(username: string) {
    this.myWebSocket = webSocket('wss://www.karchan.org/karchangame/chat');
    this.connectionOpen = true;
    this.myWebSocket.subscribe(
      msg => this.receive(msg),
      err => this.error(err),
      () => this.close()
    );
    this.toastService.show('Opening connection...', {
      delay: 5000,
      autohide: true,
      headertext: 'Opening...'
    });
  }

  /**
   *
   * Called whenever there is a message from the server
   * @param data
   */
  receive(data: Message) {
    if (window.console) { console.log(data); }
    this.messages.push(data);
  }

  send(data: Message) {
    this.myWebSocket.next(data);
  }

  /**
   * Called when connection is closed (for whatever reason)
   */
  close() {
    this.connectionOpen = false;
    this.toastService.show('Closing connection...', {
      delay: 5000,
      autohide: true,
      headertext: 'Closing...'
    });

  }


  /**
   * Called if WebSocket API signals some kind of error
   */
  error(error) {
    if (window.console) { console.log(error); }
    this.toastService.show('An error occurred using a websocket...', {
      delay: 0,
      autohide: false,
      headertext: 'Network error',
      classname: 'bg-danger text-light'
    });
  }

  isConnectionOpen(): boolean {
    return this.connectionOpen;
  }

  getMessages() {
    return this.messages;
  }

  clearMessages() {
    this.messages = [];
  }
}
