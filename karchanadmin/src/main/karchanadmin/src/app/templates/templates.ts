import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {ToastService} from '../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {TemplatesRestService} from '../templates-rest.service';
import {Template} from './template.model';
import {AgGridAngular} from 'ag-grid-angular';
import {rowSelection} from '../aggrid.utils';
import {UnifiedDiffComponent} from 'ngx-diff';

export interface TemplateData {
  name: string;
  comment: string;
  content: string;
}

@Component({
  selector: 'app-templates',
  imports: [AgGridAngular, FormField, UnifiedDiffComponent],
  templateUrl: './templates.html',
  styleUrl: './templates.css',
})
class Templates extends AdminComponent<Template, number> {
  override restService = inject(TemplatesRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "id", filter: true, width: 150},
    {field: "name", filter: true, width: 150},
    {field: "version", filter: true, width: 150},
    {field: "created", width: 150},
    {field: "modified", width: 150},
    {field: "editor", filter: true, width: 150},
  ];

  templateModel = signal<TemplateData>({
    comment: "",
    content: "",
    name: ""
  });

  form = form(this.templateModel);

  oldDocumentContents = signal<string>("");
  newDocumentContents = signal<string>("");

  historyItem = signal<Template>(new Template());

  history = signal<Template[]>([]);

  override setForm(): void {
    const template = this.item();
    this.templateModel.set({
      comment: template.comment ?? "",
      content: template.content ?? "",
      name: template.name ?? "",
    });
    this.history.set([]);
  }

  getHistory(): boolean {
    this.restService.getHistoricTemplates(this.item()).subscribe({
      next: (result: Template[]) => { // on success
        this.history.set(result);
        this.toastService.showRetrieving("History successfully retrieved.", "History retrieved...");
      },
    });
    return false;
  }

  override makeItem(): Template {
    return new Template();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].id);
  }

  onHistorySelectionChanged(event: SelectionChangedEvent): void {
    this.historyItem.set(event.api.getSelectedRows()[0]);
  }

  override getForm(): Template {
    const template = this.item();
    const formModel = this.templateModel();
    template.comment = formModel.comment == "" ? null : formModel.comment;
    template.content = formModel.content == "" ? null : formModel.content;
    template.name = formModel.name;
    return template;
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

  compare(oldTemplate: Template, newTemplate: Template | null) {
    if (newTemplate == null) {
      return;
    }
    if (oldTemplate.content == null) {
      this.oldDocumentContents.set("");
    } else {
      this.oldDocumentContents.set(oldTemplate.content);
    }
    if (newTemplate.content == null) {
      this.newDocumentContents.set("");
    } else {
      this.newDocumentContents.set(newTemplate.content);
    }
  }

  compareCurrent() {
    this.compare(this.historyItem(), this.item());
  }

  comparePrevious() {
    const index = this.history().indexOf(this.historyItem());
    const previousItem = this.history()[index-1];
    this.compare(previousItem, this.historyItem());
  }

  protected readonly rowSelection = rowSelection;

}

export default Templates
