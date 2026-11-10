/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import {
  Component,
  computed,
  DOCUMENT,
  effect,
  inject,
  OnInit,
  Renderer2,
  Signal
} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Theme } from '@core/models/theme';
import { SessionStore } from '@core/stores/session.store';
import { SystemPreferencesStore } from '@core/stores/system-preferences.store';
import { UserPreferencesStore } from '@core/stores/user-preferences.store';
import { SplashLogoComponent } from '@shared/components/splash-logo.component';

interface PreferencesStore {
  theme: Signal<Theme>;
  loading: Signal<boolean>;
}

/**
 * Componente principal que inyecta el RouterOutlet.
 *
 * @see https://angular.dev/reference/configs/file-structure
 */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SplashLogoComponent],
  template: `
    @if (preferencesStore().loading()) {
      <app-splash-logo [loaded]="!preferencesStore().loading()" />
    } @else {
      <router-outlet />
    }
  `,
})
export class App implements OnInit {
  private readonly document = inject(DOCUMENT);
  private readonly renderer = inject(Renderer2);

  private readonly sessionStore = inject(SessionStore);
  private readonly userPreferencesStore = inject(UserPreferencesStore);
  private readonly systemPreferencesStore = inject(SystemPreferencesStore);

  protected readonly preferencesStore = computed<PreferencesStore>(() => {
    if (this.sessionStore.user() != null) {
      return this.userPreferencesStore;
    }

    return this.systemPreferencesStore;
  });

  constructor() {
    // Efecto que reacciona a los cambios de Theme.
    effect(() => {
      const element = this.document.documentElement;
      this.renderer.removeClass(element, 'light');
      this.renderer.removeClass(element, 'dark');

      const theme = this.preferencesStore().theme();
      if (theme !== 'system') {
        this.renderer.addClass(element, theme);
      }
    });
  }

  ngOnInit(): void {
    this.systemPreferencesStore.loadPreferences();
  }
}
