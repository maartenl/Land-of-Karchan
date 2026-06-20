import {Component, effect, input, model, signal} from '@angular/core'
import {FormsModule} from '@angular/forms'
import {FormValueControl} from '@angular/forms/signals'
import {AngularEditorConfig, AngularEditorModule} from '@kolkov/angular-editor'

const DEFAULT_CONFIG: AngularEditorConfig = {
  editable: true,
  spellcheck: true,
  height: '15rem',
  minHeight: '5rem',
  enableToolbar: true,
  showToolbar: true,
  placeholder: 'Enter text here...',
  translate: 'no',
  defaultParagraphSeparator: 'p',
  toolbarPosition: 'top',
};

@Component({
  selector: 'app-rich-text-editor',
  imports: [AngularEditorModule, FormsModule],
  template: `
    <angular-editor
      class="overflow-hidden"
      [ngModel]="value()"
      (ngModelChange)="value.set($event)"
      [config]="mergedConfig()"
      (blur)="onBlur()"
    />
  `,
  styles: `
    :host {
      display: block;
    }
  `,
})
export class RichTextEditor implements FormValueControl<string> {
  // --- FormValueControl required property ---
  readonly value = model<string>('')

  // --- FormValueControl optional state properties ---
  readonly disabled = input<boolean>(false)
  readonly touched = model<boolean>(false)

  // --- Component-specific inputs ---
  /** Partial AngularEditorConfig merged on top of defaults. */
  readonly config = input<Partial<AngularEditorConfig>>({})

  protected readonly mergedConfig = signal<AngularEditorConfig>(DEFAULT_CONFIG)

  constructor() {
    // Reflect disabled state into editor config
    effect(() => {
      const isDisabled = this.disabled()
      const userConfig = this.config()

      this.mergedConfig.set({
        ...DEFAULT_CONFIG,
        ...userConfig,
        editable: !isDisabled,
      })
    })
  }

  protected onBlur(): void {
    this.touched.set(true)
  }
}
