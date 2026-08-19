import { Component, DOCUMENT, effect, inject, Renderer2, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SettingsStore } from '@core/store/settings.store';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('UBUMonitorWeb');

  private renderer = inject(Renderer2);
  private document = inject(DOCUMENT);
  private settingsStore = inject(SettingsStore);

  private get settings() {
    return this.settingsStore.$value();
  }

  constructor() {
    effect(() => {
      const theme = this.settings.theme;
      const htmlElement = this.document.documentElement;

      this.renderer.removeClass(htmlElement, 'light');
      this.renderer.removeClass(htmlElement, 'dark');

      if (theme !== 'system') {
        this.renderer.addClass(htmlElement, theme);
      }
    });
  }
}
