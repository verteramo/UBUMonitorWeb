/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { Component, computed, inject, Signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Theme } from '@core/models/theme';
import { SessionStore } from '@core/stores/session.store';
import { SystemPreferencesStore } from '@core/stores/system-preferences.store';
import { UserPreferencesStore } from '@core/stores/user-preferences.store';

interface PreferencesStore {
  theme: Signal<Theme>;
  nextTheme: Signal<Theme>;
  toggleTheme: () => void;
}

/**
 * Componente de botón para cambiar el tema visual de la aplicación.
 */
@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [MatButtonModule, MatIconModule],
  template: `
    <button
      mat-icon-button
      (click)="prefsStore().toggleTheme()"
      title="Switch to {{ prefsStore().nextTheme() }} mode"
    >
      <mat-icon>{{ icons[prefsStore().theme()] }}</mat-icon>
    </button>
  `,
})
export class ThemeToggleComponent {
  private readonly sessionStore = inject(SessionStore);
  private readonly userPrefsStore = inject(UserPreferencesStore);
  private readonly systemPrefsStore = inject(SystemPreferencesStore);

  protected readonly prefsStore = computed<PreferencesStore>(() => {
    if (this.sessionStore.user() != null) {
      return this.userPrefsStore;
    }

    return this.systemPrefsStore;
  });

  /**
   * Iconos de Angular Material asociados a cada tema visual.
   */
  readonly icons = {
    system: 'brightness_auto',
    light: 'light_mode',
    dark: 'dark_mode',
  };
}
