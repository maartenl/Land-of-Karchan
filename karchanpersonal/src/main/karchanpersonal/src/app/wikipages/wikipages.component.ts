import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { PlayerService } from '../player.service';
import { Wikipage } from './wikipage.model';
import { Observable } from 'rxjs';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';

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

  constructor(private playerService: PlayerService,
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
      parentTitle: null
    };
    this.isNew = true;
    this.createForms();
  }

  ngOnInit() {
    let title: string;
    const thingy: Observable<Wikipage> = this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        title = params.get('title');
        return this.playerService.getWikipage(title);
      }));

    thingy
      .subscribe(
        (result: Wikipage) => { // on success
          result.createDate = result.createDate.replace('[UTC]', '');
          result.modifiedDate = result.modifiedDate.replace('[UTC]', '');
          this.wikipage = result;
          this.isNew = false;
          this.resetForm(result);
        },
        (err: any) => { // error
          this.isNew = true;
          this.wikipage = {
            title,
            name: '',
            createDate: null,
            modifiedDate: null,
            version: '1.0',
            content: '',
            summary: '',
            parentTitle: null
          };
        },
        () => { // on completion
        }
      );
  }

  createForms() {
    this.form = this.formBuilder.group({
      title: '',
      content: '',
      summary: '',
      parentTitle: null
    });
  }

  resetForm(wikipage: Wikipage) {
    this.form.reset({
      title: wikipage.title,
      content: wikipage.content,
      summary: wikipage.summary,
      parentTitle: wikipage.parentTitle,
    });
  }

  isReadOnly(): boolean {
    return !this.isNew;
  }

  save() {
    const newWikipage = this.prepareSaveWikipage();
    if (this.isNew) {
      this.playerService.createWikipage(newWikipage).subscribe();
    } else {
      this.playerService.updateWikipage(newWikipage).subscribe();
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
      version: this.wikipage.version
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
      parentTitle: null
    };
    this.isNew = true;
    this.createForms();
  }
}