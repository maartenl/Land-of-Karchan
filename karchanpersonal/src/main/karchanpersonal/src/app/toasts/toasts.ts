import {Component, inject, TemplateRef} from '@angular/core';
import { ToastService } from '../toast.service';
import {NgbToast} from '@ng-bootstrap/ng-bootstrap';

/**
 * See https://ng-bootstrap.github.io/#/components/toast/examples#howto-global
 */
@Component({
  selector: 'app-toasts',
  imports: [NgbToast],
  template: `
		@for (toast of toastService.toasts(); track toast) {
			<ngb-toast
				[class]="toast.classname"
				[header]="toast.headertext || ''"
				[autohide]="toast.autohide || true"
				[delay]="toast.delay || 5000"
				(hidden)="toastService.remove(toast)"
			>
				{{toast.template}}
			</ngb-toast>
		}
	`,
  host: { class: 'toast-container position-fixed top-0 end-0 p-3', style: 'z-index: 1200' },
})
export class Toasts {
  toastService = inject(ToastService);

}
