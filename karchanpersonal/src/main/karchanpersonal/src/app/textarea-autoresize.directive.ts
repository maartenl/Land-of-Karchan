import { Directive, HostListener, ElementRef, OnInit } from '@angular/core';

/**
 * See https://medium.com/@chandrahasstvs/building-your-own-text-area-auto-resize-directive-in-angular-bbe3e5144e97
 */
@Directive({
  selector: '[appTextareaAutoresize]',
  standalone: false
})
export class TextareaAutoresizeDirective implements OnInit {

  constructor(private elementRef: ElementRef) { }

  ngOnInit() {
    if (this.elementRef.nativeElement.scrollHeight) {
      setTimeout(() => this.resize());
    }
  }

  @HostListener(':input')
  onInput() {
    this.resize();
  }

  resize() {
    this.elementRef.nativeElement.style.height = '0';
    this.elementRef.nativeElement.style.height = this.elementRef.nativeElement.scrollHeight + 'px';
  }
}
