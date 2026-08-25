/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

/** Themes disponibles. */
type Theme = 'system' | 'light' | 'dark';

/** Ciclo de themes para saber cuál es el siguiente. */
const nextTheme: Record<Theme, Theme> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};

/** Propiedades de estado de la aplicación. */
type AppState = {
  /** Theme de la aplicación. */
  theme: Theme;
};

/** Estado inicial. */
const initialState: AppState = {
  theme: 'system',
};

/** Store de propiedades de estado de la aplicación (no ligadas a sesión). */
export const AppStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withStorage(localStorage, 'app-state'),
  withComputed((store) => ({
    /** Theme siguiente. */
    nextTheme: computed(() => nextTheme[store.theme()]),
  })),
  withMethods((store) => ({
    /** Cambia los themes en ciclo. */
    toggleTheme() {
      patchState(store, { theme: store.nextTheme() });
    },
  })),
);
