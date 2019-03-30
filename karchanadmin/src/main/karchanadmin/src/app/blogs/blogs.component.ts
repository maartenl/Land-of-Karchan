import { Component, OnInit } from '@angular/core';
import { Blog } from './blog.model';
import { BlogService } from '../blog.service';

@Component({
  selector: 'app-blogs',
  templateUrl: './blogs.component.html',
  styleUrls: ['./blogs.component.css']
})
export class BlogsComponent implements OnInit {

  blogs: Blog[];

  blog: Blog;

  constructor(private blogService: BlogService) { }

  ngOnInit() {
    // retrieve the next page of mails starting from the last mail in the array
    this.blogService.getBlogs().subscribe(
      (result: Blog[]) => { // on success
        if (result !== undefined && result.length !== 0) {
          result.forEach((value) => {
            value.creation = value.creation.replace('[UTC]', '');
            value.modification = value.modification.replace('[UTC]', '');
          });
          this.blogs = result;
        }
      },
      (err: any) => { // error
        // console.log('error', err);
      },
      () => { // on completion
      }
    );
  }

  public setBlog(blog: Blog): void {
    this.blog = blog;
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

}
