/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AppStore } from '@core/stores/app.store';
/** Botón para cambiar el Theme. */
@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [MatButtonModule, MatIconModule],
  template: `
    <button mat-icon-button (click)="store.toggleTheme()" [title]="title()">
      <mat-icon>{{ icons[store.theme()] }}</mat-icon>
    </button>
  `,
})
export class ThemeToggleComponent {
  /** Store de las preferencias de la aplicación. */
  readonly store = inject(AppStore);

  /** Iconos de Angular Material asociados a los themes. */
  readonly icons = {
    system: 'brightness_auto',
    light: 'light_mode',
    dark: 'dark_mode',
  };

  /** Título del botón para cambiar de theme. */
  title = computed(() => {
    return $localize`Switch to ${this.store.nextTheme()} mode`;
  });
}
