import {Injectable} from '@angular/core';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {ToastService} from './toast.service';
import {Log} from './play/log.model';
import {Logger} from "./consolelog.service";

export class Message {
  fileLength: number | null = null;
  from: string = "";
  to: string = "";
  content: string = "";
  type: string = "";

  constructor() {
  }

}

interface ChatlogServiceInterface {

  getMessages(): Message[];

  clearMessages(): void;

  ping(): void;

  open(username: string): void;

  reconnect(): void;

  close(): void;

}

class WebsocketChatlogService implements ChatlogServiceInterface {

  private username: string = "";
  private interval: number | null = null;
  private connectionOpen: boolean = false;

  private messages: Message[] = [];

  private myWebSocket: WebSocketSubject<Message> | null = null;

  private timeout: number | null = null;

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
    Logger.log("Opening websocket to " + url.href);
    url.href // => ws://www.example.com:9999/path/to/websocket
    this.myWebSocket = webSocket(url.href);
    this.connectionOpen = true;
    const self = this;
    this.myWebSocket.subscribe({
      next: (msg) => self.receive(msg),
      error: (err) => self.error(err),
      complete: () => self.closingConnection()
    });
    this.setInterval();
  }

  private setInterval() {
    const self = this;
    if (this.interval !== null) {
      Logger.log("Clearing internal ping interval " + this.interval);
      window.clearInterval(this.interval);
      this.interval = null;
    }
    this.interval = window.setInterval(function () {
      self.internalping(self);
    }, 30000);
    Logger.log("Setting internal ping interval " + this.interval);
  }

  ping() {
    const message = new Message();
    message.content = "ping";
    message.from = this.username;
    message.type = "ping";
    this.send(message);
  }

  private internalping(chatLogService: WebsocketChatlogService) {
    Logger.log(chatLogService.counter + ": sent internal ping");
    const message = new Message();
    message.content = "internalping";
    message.from = this.username;
    message.type = "internalping";
    chatLogService.send(message);
    chatLogService.timeout = window.setTimeout(function () {
      /// ---did not receive pong in 5 seconds! ///
      if (window.console) {
        Logger.log(chatLogService.counter + ": did not receive internal pong in 5 seconds");
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
    Logger.logObject(data);
    if (this.timeout !== null) {
      Logger.log("message received - resetting internal ping interval");
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
      Logger.log((this.counter++) + ": received internal pong");
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
    Logger.logError("error in websocket com", error);
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

  clearMessages(): void {
    this.messages = [];
  }
}

class NoWebsocketChatlogService implements ChatlogServiceInterface {

  clearMessages() {
  }

  getMessages(): Message[] {
    return [];
  }

  ping(): void {
  }

  open(username: string): void {
  }

  reconnect(): void {
  }

  close(): void {
  }
}

@Injectable({
  providedIn: 'root'
})
export class ChatlogService {

  private chatlogService: ChatlogServiceInterface;

  private log: Log = new Log();

  private enabled: boolean = true;

  constructor(
    private toastService: ToastService
  ) {
    this.chatlogService = new WebsocketChatlogService(toastService)
  }

  public isWebsocketsEnabled(): boolean {
    Logger.log("chatlogService: websockets enabled=" + this.enabled);
    return this.enabled;
  }

  public enableWebsockets() {
    Logger.log("chatlogService: websockets enabled.");
    this.enabled = true;
    this.chatlogService = new WebsocketChatlogService(this.toastService);
  }

  public disableWebsockets() {
    Logger.log("chatlogService: websockets disabled.");
    this.enabled = false;
    this.chatlogService = new NoWebsocketChatlogService();
  }

  /**
   * Used in the chatlog component to display the log.
   * Only contains stuff, if websockets is on.
   */
  getMessages(): Message[] {
    return this.chatlogService.getMessages();
  }

  clear(): void {
    this.log = new Log();
    this.chatlogService.clearMessages();
  }

  setLog(log: Log): void {
    Logger.log('setLog');
    Logger.logObject(log);
    this.log = log;
  }

  getOffset(): number {
    return this.log.offset;
  }

  getLog(): Log {
    return this.log;
  }

  ping(): void {
    this.chatlogService.ping();
  }

  open(username: string): void {
    this.chatlogService.open(username);
  }

  reconnect(): void {
    this.chatlogService.reconnect();
  }

  close(): void {
    this.chatlogService.close();
  }
}
