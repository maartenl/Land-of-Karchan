import {ChangeDetectionStrategy, Component, signal} from '@angular/core';
import {ICellRendererAngularComp} from 'ag-grid-angular';
import type { ICellRendererParams } from 'ag-grid-community';
import {RouterLink} from '@angular/router';

interface CustomParams {
  prefix: string;
}
type ICellRendererParamsWithPrefix = ICellRendererParams & CustomParams;

@Component({
  standalone: true,
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: ` <a [routerLink]="[prefix(), value()]">{{ value() }}</a> `,
})
export class LinkRenderer implements ICellRendererAngularComp {
  value = signal("");
  prefix = signal("");

  agInit(params: ICellRendererParamsWithPrefix): void {
    this.refresh(params);
  }

  refresh(params: ICellRendererParamsWithPrefix): boolean {
    this.value.set(params.value);
    this.prefix.set(params.prefix);
    // As we have updated the params we return true to let AG Grid know we have handled the refresh.
    // So AG Grid will not recreate the cell renderer from scratch.
    return true;
  }
}
