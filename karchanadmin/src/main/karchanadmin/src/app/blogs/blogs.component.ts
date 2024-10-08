import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {Blog} from './blog.model';
import {BlogService} from '../blog.service';

@Component({
  selector: 'app-blogs',
  templateUrl: './blogs.component.html',
  styleUrls: ['./blogs.component.css']
})
export class BlogsComponent implements OnInit {

  blogs: Blog[] = new Array<Blog>(0);

  blog: Blog | null = null;

  blogForm: FormGroup;

  constructor(private blogService: BlogService,
              private formBuilder: FormBuilder) {
    this.blogForm = this.formBuilder.group({
      title: '',
      urlTitle: '',
      contents: ''
    });
    this.blog = new Blog();
  }

  ngOnInit() {
    this.blogService.getBlogs().subscribe({
        next: (result: Blog[]) => { // on success
          if (result !== undefined && result.length !== 0) {
            result.forEach((value) => {
              if (value.creation !== null) {
                value.creation = value.creation.replace('[UTC]', '');
              }
              if (value.modification !== null) {
                value.modification = value.modification.replace('[UTC]', '');
              }
            });
            this.blogs = result;
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
    this.blogForm = this.formBuilder.group({
      title: '',
      urlTitle: '',
      contents: ''
    });
  }

  resetForm() {
    this.blogForm.reset({
      title: '',
      urlTitle: '',
      contents: ''
    });
  }

  public cancel(): void {
    this.resetForm();
    this.blog = new Blog();
  }

  public setBlog(blog: Blog): void {
    this.blog = blog;
    this.blogForm.reset({
      title: blog.title,
      urlTitle: blog.urlTitle,
      contents: blog.contents
    });
  }

  public deleteBlog(blog: Blog): void {
    this.blogService.deleteBlog(blog).subscribe(
      (result: any) => { // on success
        this.blogs = this.blogs.filter((bl) => bl.id === blog.id);
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public saveBlog(): void {
    if (this.blog === null) {
      return;
    }
    const index = this.blogs.indexOf(this.blog);
    const blog = this.prepareSave();
    if (blog === null) {
      return;
    }
    this.blogService.updateBlog(blog).subscribe(
      (result: any) => { // on success
        if (blog.id === undefined) {
          this.blogs.unshift(blog);
        } else {
          this.blogs[index] = blog;
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  prepareSave(): Blog | null {
    if (this.blog === null) {
      return null;
    }
    const formModel = this.blogForm.value;

    // return new `Blog` object containing a combination of original blog value(s)
    // and deep copies of changed form model values
    const saveBlog: Blog = {
      id: this.blog.id as number,
      title: formModel.title as string,
      urlTitle: formModel.urlTitle as string,
      contents: formModel.contents as string,
      creation: this.blog.creation as string,
      modification: this.blog.modification as string,
      name: this.blog.name as string
    };
    return saveBlog;
  }
}
