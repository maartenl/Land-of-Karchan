import { Injectable, WritableSignal, effect, signal } from "@angular/core";
import {CookieService} from "ngx-cookie-service";

/**
 * https://medium.com/@davdifr/theme-switcher-in-angular-from-dark-to-light-and-back-again-f42fc3f9fab0
 */
@Injectable({
  providedIn: "root",
})
export class ThemeService {
  #path: string = "assets/css";
  #stylesheet: HTMLLinkElement | null = document.getElementById(
    "pagestyle"
  ) as HTMLLinkElement;
  #darkmode: boolean = false;

  constructor(private cookieService: CookieService) {
    if (window.console) {
      console.log("ThemeService.constructor called");
    }
    this.initializeThemeFromPreferences();

    effect(() => {
      this.updateRenderedTheme();
    });
  }

  toggleTheme(): void {
    this.#darkmode = !this.#darkmode;
    if (this.#darkmode) {
      this.cookieService.set('karchandarkmode', 'true', 365, '/');
    } else {
      this.cookieService.delete('karchandarkmode', '/');
    }
    this.updateRenderedTheme()
  }

  private initializeThemeFromPreferences(): void {
    if (!this.#stylesheet) {
      this.initializeStylesheet();
    }

    this.#darkmode = this.cookieService.check('karchandarkmode');

    this.updateRenderedTheme()
  }

  private initializeStylesheet(): void {
    this.#stylesheet = document.createElement("link");
    this.#stylesheet.id = "pagestyle";
    this.#stylesheet.rel = "stylesheet";

    document.head.appendChild(this.#stylesheet);
  }

  private getCssLabel(): string {
    return `${this.isDarkThemeActive() ? ".darkmode" : ""}`;
  }

  isDarkThemeActive(): boolean {
    return this.#darkmode;
  }

  private updateRenderedTheme(): void {
    if (this.#stylesheet) {
      this.#stylesheet.href = `${this.#path}/bootstrap${this.getCssLabel()}.min.css`;
    }
  }
}
