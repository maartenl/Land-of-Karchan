import {AfterViewInit, Component, inject, OnInit, signal} from '@angular/core';
import {PlayerService} from '../player.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastService} from '../toast.service';
import {Wikipage} from './wikipage.model';
import {form, FormField} from '@angular/forms/signals';
import {DatePipe} from '@angular/common';

export interface WikipageModel {
  title: string
  content: string
  summary: string
  parentTitle: string
  administration: boolean
  comment: string
  ordering: number
}

@Component({
  selector: 'app-wikipages',
  imports: [DatePipe, FormField],
  templateUrl: './wikipages.html',
  styleUrl: './wikipages.css',
})
export class Wikipages implements OnInit {
  private playerService = inject(PlayerService)
  private route = inject(ActivatedRoute)
  private router = inject(Router)
  private toastService = inject(ToastService)

  wikipage = signal<Wikipage>({
    name: '',
    title: '',
    content: '',
    comment: '',
    createDate: null,
    administration: false,
    modifiedDate: null,
    parentTitle: '',
    ordering: 0,
    summary: '',
    version: '',
  })

  wikipageModel = signal<WikipageModel>({
    title: '',
    content: '',
    summary: '',
    parentTitle: '',
    administration: false,
    comment: '',
    ordering: 0
  })

  wikipageForm = form(this.wikipageModel);

  isNew = signal(true);

  previewHtml = signal("");

  ngOnInit() {
    const title: string | null = this.route.snapshot.paramMap.get('title');
    if (title !== null) {
      this.setWikipage(title);
    }
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
          this.isNew.set(false);
          this.wikipage.set(Object.assign({}, result));
          const wikipageModel: WikipageModel = {
            administration: result.administration,
            comment: result.comment,
            content: result.content,
            ordering: result.ordering,
            parentTitle: result.parentTitle ?? '',
            summary: result.summary,
            title: result.title
          };
          this.wikipageModel.set(wikipageModel);
        },
        error: (err: any) => { // error
          if (title !== "") {
            this.toastService.showError("Wikipage not found.", "Not found.");
          }
          this.isNew.set(true);
          this.wikipageModel.set(this.createEmptyWikipage(title));
        }
      });
  }

  private createEmptyWikipage(title1: string): WikipageModel {
    return {
      title: title1,
      content: '',
      summary: '',
      parentTitle: '',
      administration: false,
      comment: '',
      ordering: 0
    };
  }

  preview() {
    const formModel = this.wikipageModel();
    this.playerService.getWikipagePreview(formModel.content).subscribe({
      next: (formattedText: string) => {
        this.previewHtml.set(formattedText);
      }
    });
  }

  save(event: Event) {
    event.preventDefault();
    const newWikipageModel = this.wikipageModel();
    const newWikipage = this.wikipage();
    newWikipage.title = newWikipageModel.title;
    newWikipage.name = newWikipageModel.title;
    newWikipage.content = newWikipageModel.content;
    newWikipage.summary = newWikipageModel.summary;
    newWikipage.parentTitle = newWikipageModel.parentTitle;
    newWikipage.administration = newWikipageModel.administration;
    newWikipage.comment = newWikipageModel.comment;
    newWikipage.ordering = newWikipageModel.ordering;
    if (this.isNew()) {
      this.playerService.createWikipage(newWikipage).subscribe({
        complete: () => {
          this.setWikipage(newWikipage.title);
          this.toastService.showMessage("Wikipage created.", "Success...");
        }
      });
    } else {
      this.playerService.updateWikipage(newWikipage).subscribe({
        complete: () => {
          this.setWikipage(newWikipage.title)
          this.toastService.showMessage("Wikipage updated.", "Success...");
        }
      });
    }
  }

  cancel() {
    this.wikipageModel.set(this.createEmptyWikipage("empty"));
    this.isNew.set(true);
  }

  delete() {
    const newWikipage = this.wikipageModel();
    this.playerService.deleteWikipage(newWikipage.title).subscribe({
      complete: () => this.toastService.showMessage("Wikipage deleted.", "Success...")
    });
  }

  isDeputy(): boolean {
    return this.playerService.isDeputy();
  }

  hasComment(): boolean {
    var wikipage = this.wikipageModel();
    return wikipage.comment.trim() !== '';
  }

}
