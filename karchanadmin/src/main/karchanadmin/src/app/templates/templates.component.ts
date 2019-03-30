import { Component, OnInit } from '@angular/core';

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

  constructor(private templateService: TemplateService) { }

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

  public setTemplate(template: Template): void {
    this.template = template;
  }

  public deleteTemplate(template: Template): void {
    this.templateService.deleteTemplate(template).subscribe(
      (result: any) => { // on success
        this.templates = this.templates.filter((bl) => bl.id === template.id);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

}
