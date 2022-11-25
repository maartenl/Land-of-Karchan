import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {ToastService} from './toast.service';

export class Message {
  from: string = "";
  to: string = "";
  content: string = "";
  type: string = "";

  constructor() {
  }

}

@Injectable({
  providedIn: 'root'
})
export class ChatlogService {

  private connectionOpen: boolean = false;

  private myWebSocket: WebSocketSubject<Message> | null = null;

  private username: string = "";

  private messages: Message[] = [];

  private timeout: number | null = null;
  private interval: number | null = null;

  private counter: number = 0;

  constructor(
    private toastService: ToastService
  ) {
  }

  open(username: string) {
    this.internalopen(username);
    this.toastService.show('Opening connection...', {
      delay: 5000,
      autohide: true,
      headertext: 'Opening...'
    });
  }

  private internalopen(username: string) {
    this.username = username;
    const url = new URL('/karchangame/chat', window.location.href);
    url.protocol = url.protocol.replace('http', 'ws');
    url.protocol = url.protocol.replace('https', 'wss');
    if (window.console) {
      console.log("Opening websocket to " + url.href);
    }
    url.href // => ws://www.example.com:9999/path/to/websocket
    this.myWebSocket = webSocket(url.href);
    this.connectionOpen = true;
    this.myWebSocket.subscribe(
      msg => this.receive(msg),
      err => this.error(err),
      () => this.closingConnection()
    );
    this.setInterval();
  }

  private setInterval() {
    const self = this;
    if (this.interval !== null) {
      console.log("Clearing internal ping interval " + this.interval);
      window.clearInterval(this.interval);
      this.interval = null;
    }
    this.interval = window.setInterval(function () {
      self.internalping(self);
    }, 30000);
    console.log("Setting internal ping interval " + this.interval);
  }

  ping() {
    const message = new Message();
    message.content = "ping";
    message.from = this.username;
    message.type = "ping";
    this.send(message);
  }

  private internalping(chatLogService: ChatlogService) {
    if (window.console) {
      console.log(chatLogService.counter + ": sent internal ping");
    }
    const message = new Message();
    message.content = "internalping";
    message.from = this.username;
    message.type = "internalping";
    chatLogService.send(message);
    const ourselves = this;
    chatLogService.timeout = window.setTimeout(function () {
      /// ---did not receive pong in 5 seconds! ///
      if (window.console) {
        console.log(chatLogService.counter + ": did not receive internal pong in 5 seconds");
        chatLogService.counter++;
      }
      chatLogService.close();
      chatLogService.internalopen(chatLogService.username);
    }, 5000);
  }

  reconnect() {
    this.open(this.username);
  }

  /**
   *
   * Called whenever there is a message from the server
   * @param data
   */
  receive(data: Message) {
    if (window.console) {
      console.log(data);
    }
    if (this.timeout !== null) {
      if (window.console) {
        console.log("message received - resetting internal ping interval");
      }
      window.clearTimeout(this.timeout);
      this.timeout = null;
      this.setInterval();
    }
    if (data.type === "chat") {
      this.messages.push(data);
    }
    if (data.type === "info") {
      this.toastService.show(data.content, {
        delay: 5000,
        autohide: true
      });
    }
    if (data.type === "pong") {
      this.toastService.show("Pong!", {
        delay: 5000,
        autohide: true
      });
    }
    if (data.type === "internalpong") {
      if (window.console) {
        console.log((this.counter++) + ": received internal pong");
      }
    }
  }

  send(data: Message) {
    if (this.myWebSocket !== null) {
      this.myWebSocket.next(data);
    }
  }

  /**
   * Closes the websocket.
   */
  close() {
    this.myWebSocket?.unsubscribe();
  }

  /**
   * Called when connection is closed (for whatever reason)
   */
  closingConnection() {
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
  error(error: any) {
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
