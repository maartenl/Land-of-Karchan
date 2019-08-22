import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { PlayerService } from '../player.service';
import { Wikipage } from './wikipage.model';
// import { Observable } from 'rxjs';
import { FormGroup, FormBuilder } from '@angular/forms';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-wikipages',
  templateUrl: './wikipages.component.html',
  styleUrls: ['./wikipages.component.css']
})
export class WikipagesComponent implements OnInit {

  /**
   * the model
   */
  wikipage: Wikipage;

  isNew: boolean;

  form: FormGroup;

  constructor(
    private playerService: PlayerService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private router: Router) {
    this.wikipage = {
      title: 'Unknown',
      name: '',
      createDate: null,
      modifiedDate: null,
      version: '0',
      content: '',
      summary: '',
      parentTitle: null,
      administration: false,
      comment: undefined
    };
    this.isNew = true;
    this.createForms();
  }

  ngOnInit() {
    const title: string = this.route.snapshot.paramMap.get('title');
    this.setWikipage(title);
  }

  getWebsite(): string {
    return environment.website;
  }
  
  private setWikipage(title: string) {
    this.playerService.getWikipage(title)
      .subscribe({
        next: (result: Wikipage) => { // on success
          result.createDate = result.createDate.replace('[UTC]', '');
          result.modifiedDate = result.modifiedDate.replace('[UTC]', '');
          this.wikipage = result;
          this.isNew = false;
          this.resetForm(result);
        },
        error: (err: any) => { // error
          this.isNew = true;
          this.wikipage = {
            title,
            name: '',
            createDate: null,
            modifiedDate: null,
            version: '1.0',
            content: '',
            summary: '',
            parentTitle: null,
            administration: false,
            comment: undefined
          };
        }
      });
  }

  createForms() {
    this.form = this.formBuilder.group({
      title: '',
      content: '',
      summary: '',
      parentTitle: null,
      administration: false,
      comment: ''
    });
  }

  resetForm(wikipage: Wikipage) {
    this.form.reset({
      title: wikipage.title,
      content: wikipage.content,
      summary: wikipage.summary,
      parentTitle: wikipage.parentTitle,
      administration: wikipage.administration,
      comment: wikipage.comment
    });
  }

  isReadOnly(): boolean {
    return !this.isNew;
  }

  save() {
    const newWikipage = this.prepareSaveWikipage();
    if (this.isNew) {
      this.playerService.createWikipage(newWikipage).subscribe({
         complete: () => this.setWikipage(newWikipage.title)
        });
    } else {
      this.playerService.updateWikipage(newWikipage).subscribe({
        complete: () => this.setWikipage(newWikipage.title)
      });
    }
  }

  prepareSaveWikipage(): Wikipage {
    const formModel = this.form.value;

    // return new `Wikipage` object containing a combination of original value(s)
    // and deep copies of changed form model values
    const saveWikipage: Wikipage = {
      title: formModel.title as string,
      content: formModel.content as string,
      parentTitle: formModel.parentTitle as string,
      summary: formModel.summary as string,
      createDate: this.wikipage.createDate,
      modifiedDate: this.wikipage.modifiedDate,
      name: this.wikipage.name,
      version: this.wikipage.version,
      administration: formModel.administration as boolean,
      comment: formModel.comment as string
    };
    return saveWikipage;
  }

  cancel() {
    this.wikipage = {
      title: 'Unknown',
      name: '',
      createDate: null,
      modifiedDate: null,
      version: '0',
      content: '',
      summary: '',
      parentTitle: null,
      administration: false,
      comment: undefined
    };
    this.isNew = true;
    this.createForms();
  }

  isDeputy(): boolean {
    return this.playerService.isDeputy();
  }
}
