import { Component, OnInit, TemplateRef } from '@angular/core';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-toasts',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  host: { '[class.ngb-toasts]': 'true' }
})
export class ToastComponent implements OnInit {

  constructor(public toastService: ToastService) { }

  ngOnInit() {
  }

  isTemplate(toast: any) { return toast.textOrTpl instanceof TemplateRef; }
}
