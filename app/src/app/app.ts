/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, DOCUMENT, effect, inject, OnInit, Renderer2 } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SystemPreferencesStore } from '@core/stores/system-preferences.store';
import { SplashLogoComponent } from '@shared/components/splash-logo.component';

/**
 * Componente principal que inyecta el RouterOutlet.
 *
 * @see https://angular.dev/reference/configs/file-structure
 */
@Component({
  selector: 'app-root',

  imports: [RouterOutlet, SplashLogoComponent],
  template: `
    @if (store.loading()) {
      <app-splash-logo [loaded]="!store.loading()" />
    } @else {
      <router-outlet />
    }
  `,
})
export class App implements OnInit {
  protected readonly store = inject(SystemPreferencesStore);
  private readonly document = inject(DOCUMENT);
  private readonly renderer = inject(Renderer2);

  constructor() {
    // Efecto que reacciona a los cambios de Theme.
    effect(() => {
      const element = this.document.documentElement;
      this.renderer.removeClass(element, 'light');
      this.renderer.removeClass(element, 'dark');

      const theme = this.store.theme();
      if (theme !== 'system') {
        this.renderer.addClass(element, theme);
      }
    });
  }

  ngOnInit(): void {
    this.store.loadPreferences();
  }
}
