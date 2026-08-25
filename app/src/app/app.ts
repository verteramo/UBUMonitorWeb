/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, DOCUMENT, effect, inject, Renderer2 } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppStore } from '@core/stores/app.store';

/**
 * Componente principal que inyecta el RouterOutlet.
 *
 * @see https://angular.dev/reference/configs/file-structure
 */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  template: `<router-outlet />`,
})
export class App {
  private readonly store = inject(AppStore);
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
}
