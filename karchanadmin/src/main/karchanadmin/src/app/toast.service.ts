import {Injectable, signal, TemplateRef} from '@angular/core';

export interface Toast {
  headertext?: string;
  template: string;
  classname?: string;
  delay?: number;
  autohide?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private readonly _toasts = signal<Toast[]>([]);
  readonly toasts = this._toasts.asReadonly();

  // Push new Toasts to array with content and options
  private showToast(toast: Toast) {
    this._toasts.update((toasts) => [...toasts, toast]);
  }

  showRetrieving(template: string, headertext: string = ""): Toast {
    const toast = {
      template: template,
      headertext: headertext,
      delay: 1,
      autohide: false,
    };
    this.showToast(toast);
    return toast;
  }

  showMessage(template: string, headertext: string = "") {
    this.showToast({
      template: template,
      headertext: headertext,
      delay: 2000,
      autohide: true,
    });
  }

  showError(template: string, headertext: string) {
    this.showToast({
      template: template,
      headertext: headertext,
      delay: 0,
      autohide: false,
      classname: 'bg-danger text-light'
    });
  }

  // Callback method to remove Toast DOM element from view
  remove(toast: Toast) {
    this._toasts.update((toasts) => toasts.filter((t) => t !== toast));
  }

  clear() {
    this._toasts.set([]);
  }
}
