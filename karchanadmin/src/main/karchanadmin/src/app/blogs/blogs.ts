import {Component, inject, signal} from '@angular/core';
import {form, FormField} from '@angular/forms/signals';
import {AdminComponent} from '../admin/admin.component';
import {ToastService} from '../toast.service';
import {ColDef, SelectionChangedEvent} from 'ag-grid-community';
import {BlogsRestService} from '../blogs-rest.service';
import {Blog} from './blog.model';
import {rowSelection} from '../aggrid.utils';
import {AgGridAngular} from 'ag-grid-angular';

export interface BlogData {
  title: string;
  urlTitle: string;
  contents: string;
}

@Component({
  selector: 'app-blogs',
  imports: [AgGridAngular, FormField],
  templateUrl: './blogs.html',
  styleUrl: './blogs.css',
})
export class Blogs extends AdminComponent<Blog, number> {
  override restService = inject(BlogsRestService);
  override toastService = inject(ToastService);

  override colDefs: ColDef[] = [
    {field: "id", filter: true, width: 150},
    {field: "title", filter: true, width: 150},
    {field: "urlTitle", filter: true, width: 150},
    {field: "creation", width: 150},
    {field: "modification", width: 150},
  ];

  blogModel = signal<BlogData>({
    title: "",
    urlTitle: "",
    contents: "",
  });

  form = form(this.blogModel);

  override setForm(): void {
    const blog = this.item();
    this.blogModel.set({
      title: blog.title ?? "",
      urlTitle: blog.urlTitle ?? "",
      contents: blog.contents ?? "",
    });
  }

  override makeItem(): Blog {
    return new Blog();
  }

  override onSelectionChanged(event: SelectionChangedEvent): void {
    this.setItemById(event.api.getSelectedRows()[0].id);
  }

  override getForm(): Blog {
    const blog = this.item();
    const formModel = this.blogModel();
    blog.title = formModel.title == "" ? null : formModel.title;
    blog.urlTitle = formModel.urlTitle == "" ? null : formModel.urlTitle;
    blog.contents = formModel.contents;
    return blog;
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

  protected readonly rowSelection = rowSelection;

}
