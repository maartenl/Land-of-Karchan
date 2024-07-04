import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PlayerService} from '../player.service';
import {Wikipage} from './wikipage.model';
// import { Observable } from 'rxjs';
import {FormBuilder, FormGroup} from '@angular/forms';
import {environment} from 'src/environments/environment';
import {ToastService} from "../toast.service";

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

  previewHtml: string = '';

  constructor(
    private playerService: PlayerService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private router: Router,
    private toastService: ToastService) {
    this.wikipage = this.createEmptyWikipage();
    this.isNew = true;
    this.form = this.formBuilder.group({
      title: '',
      content: '',
      summary: '',
      parentTitle: null,
      administration: false,
      comment: '',
      ordering: 0
    });
  }

  ngOnInit() {
    const title: string | null = this.route.snapshot.paramMap.get('title');
    if (title !== null) {
      this.setWikipage(title);
    }
  }

  getWebsite(): string {
    return environment.website;
  }

  private setWikipage(title: string) {
    this.playerService.getWikipage(title)
      .subscribe({
        next: (result: Wikipage) => { // on success
          if (result.createDate !== null) {
            result.createDate = result.createDate.replace('[UTC]', '');
          }
          if (result.modifiedDate !== null) {
            result.modifiedDate = result.modifiedDate.replace('[UTC]', '');
          }
          this.wikipage = result;
          this.isNew = false;
          this.resetForm(result);
        },
        error: (err: any) => { // error
          this.isNew = true;
          this.wikipage = this.createEmptyWikipage();
          this.wikipage.title = title;
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
      comment: '',
      ordering: 0
    });
  }

  resetForm(wikipage: Wikipage) {
    this.form.reset({
      title: wikipage.title,
      content: wikipage.content,
      summary: wikipage.summary,
      parentTitle: wikipage.parentTitle,
      administration: wikipage.administration,
      comment: '',
      ordering: wikipage.ordering
    });
  }

  isReadOnly(): boolean {
    return !this.isNew;
  }

  preview() {
    const formModel = this.form.value;
    this.playerService.getWikipagePreview(formModel.content as string).subscribe({
      next: (formattedText: string) => {
        this.previewHtml = formattedText;
      }
    });
  }

  save() {
    const newWikipage = this.prepareSaveWikipage();
    if (this.isNew) {
      this.playerService.createWikipage(newWikipage).subscribe({
        complete: () => {
          this.setWikipage(newWikipage.title);
          this.toastService.show('Wikipage created.', {
            delay: 3000,
            autohide: true,
            headertext: 'Success...'
          });
        }
      });
    } else {
      this.playerService.updateWikipage(newWikipage).subscribe({
        complete: () => {
          this.setWikipage(newWikipage.title)
          this.toastService.show('Wikipage updated.', {
            delay: 3000,
            autohide: true,
            headertext: 'Success...'
          });
        }
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
      comment: formModel.comment as string,
      ordering: formModel.ordering as number
    };
    return saveWikipage;
  }

  private createEmptyWikipage(): Wikipage {
    return {
      title: 'Unknown',
      name: '',
      createDate: null,
      modifiedDate: null,
      version: '1.0',
      content: '',
      summary: '',
      parentTitle: '',
      administration: false,
      comment: '',
      ordering: 0
    };
  }

  cancel() {
    this.wikipage = this.createEmptyWikipage();
    this.isNew = true;
    this.createForms();
  }

  delete() {
    const newWikipage = this.prepareSaveWikipage();
    this.playerService.deleteWikipage(newWikipage.title).subscribe({
      complete: () => this.toastService.show('Wikipage deleted.', {
        delay: 3000,
        autohide: true,
        headertext: 'Success...'
      })
    });
  }

  isDeputy(): boolean {
    return this.playerService.isDeputy();
  }

  hasComment(): boolean {
    return this.wikipage !== null &&
      this.wikipage !== undefined &&
      this.wikipage.comment !== null &&
      this.wikipage.comment !== undefined &&
      this.wikipage.comment.trim() !== '';
  }
}
