import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Template} from './template.model';
import {TemplateService} from '../template.service';
import {ToastService} from "../toast.service";

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css']
})
export class TemplatesComponent implements OnInit {

  templates: Template[] = new Array<Template>(0);

  history: Template[] = new Array<Template>(0);

  template: Template | null = null;

  templateForm: FormGroup;

  oldDocumentContents: string | undefined;

  newDocumentContents: string | undefined;

  constructor(private templateService: TemplateService,
              private toastService: ToastService,
              private formBuilder: FormBuilder) {
    this.templateForm = this.formBuilder.group({
      name: '',
      content: '',
      comment: ''
    });
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.templateService.getTemplates().subscribe({
        next: (result: Template[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach((value) => {
              if (value.created !== null) {
                value.created = value.created.replace('[UTC]', '');
              }
              if (value.modified !== null) {
                value.modified = value.modified.replace('[UTC]', '');
              }
            });
            this.templates = result;
          }
        },
        error: (err: any) => { // error
          // console.log('error', err);
        },
        complete: () => { // on completion
        }
      }
    );
  }

  createForm() {
    this.templateForm = this.formBuilder.group({
      name: '',
      content: '',
      comment: ''
    });
  }

  resetForm() {
    this.templateForm.reset({
      name: '',
      content: '',
      comment: ''
    });
  }

  public cancel(): void {
    this.resetForm();
    this.template = new Template();
  }

  public setTemplate(template: Template): void {
    this.template = template;
    this.history = [];
    this.oldDocumentContents = undefined;
    this.newDocumentContents = undefined;
    this.templateForm.reset({
      name: template.name,
      content: template.content,
      comment: template.comment
    });
  }

  public saveTemplate(): void {
    if (this.template === null || this.template.id === undefined) {
      return;
    }
    const index = this.templates.indexOf(this.template);
    const template = this.prepareSave();
    if (template === null) {
      return;
    }
    this.templateService.updateTemplate(template).subscribe({
      next: (result: any) => { // on success
        this.templates[index] = template;
        this.toastService.show('Template successfully updated.', {
          delay: 3000,
          autohide: true,
          headertext: 'Updated...'
        });
      },
    });
  }

  prepareSave(): Template | null {
    if (this.template === null) {
      return null;
    }
    const formModel = this.templateForm.value;

    // return new `Template` object containing a combination of original template value(s)
    // and deep copies of changed form model values
    const saveTemplate: Template = {
      id: this.template.id as number,
      content: formModel.content as string,
      created: this.template.created as string,
      modified: this.template.modified as string,
      name: this.template.name as string,
      version: this.template.version as number,
      editor: this.template.editor as string,
      comment: formModel.comment as string
    };
    return saveTemplate;
  }

  getHistory(): boolean {
    if (this.template === null) {
      return false;
    }
    this.templateService.getHistoricTemplates(this.template).subscribe({
      next: (result: Template[]) => { // on success
        this.history = [...result];
        this.toastService.show('History successfully retrieved.', {
          delay: 3000,
          autohide: true,
          headertext: 'History retrieved...'
        });
      },
    });
    return false;
  }

  compare(oldTemplate: Template, newTemplate: Template | null) {
    if (newTemplate == null) {
      return;
    }
    if (oldTemplate.content == null) {
      this.oldDocumentContents = undefined;
    } else {
      this.oldDocumentContents = oldTemplate.content;
    }
    if (newTemplate == null || newTemplate.content == null) {
      this.newDocumentContents = undefined;
    } else {
      this.newDocumentContents = newTemplate.content;
    }
  }
}

