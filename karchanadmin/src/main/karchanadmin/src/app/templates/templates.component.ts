import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

import { Template } from './template.model';
import { TemplateService } from '../template.service';

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css']
})
export class TemplatesComponent implements OnInit {

  templates: Template[];

  template: Template;

  templateForm: FormGroup;

  constructor(private templateService: TemplateService,
              private formBuilder: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.templateService.getTemplates().subscribe(
      (result: Template[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach((value) => {
            value.created = value.created.replace('[UTC]', '');
            value.modified = value.modified.replace('[UTC]', '');
          });
          this.templates = result;
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  createForm() {
    this.templateForm = this.formBuilder.group({
      name: '',
      content: ''
    });
  }

  resetForm() {
    this.templateForm.reset({
      name: '',
      content: ''
    });
  }

  public cancel(): void {
    this.resetForm();
    this.template = new Template();
  }

  public setTemplate(template: Template): void {
    this.template = template;
    this.templateForm.reset({
      name: template.name,
      content: template.content
    });
  }

  public saveTemplate(): void {
    if (this.template.id === undefined) {
      return;
    }
    const template = this.prepareSave();
    this.templateService.updateTemplate(template).subscribe();
  }

  prepareSave(): Template {
    const formModel = this.templateForm.value;

    // return new `Template` object containing a combination of original template value(s)
    // and deep copies of changed form model values
    const saveTemplate: Template = {
      id: this.template.id as number,
      content: formModel.content as string,
      created: this.template.created as string,
      modified: this.template.modified as string,
      name: this.template.name as string,
      version: this.template.version as number
    };
    return saveTemplate;
  }
}
