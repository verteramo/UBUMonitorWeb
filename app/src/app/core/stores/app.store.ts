import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withRegistrySlot } from './features/registry.feature';
import { GlobalStore } from './global.store';

/**
 * Temas visuales disponibles para la aplicación.
 */
type Theme = 'system' | 'light' | 'dark';

/**
 * Mapa de transiciones para ciclar entre los diferentes temas visuales.
 */
const nextTheme: Record<Theme, Theme> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};

/**
 * Store global de la aplicación que gestiona el estado principal, como el tema visual actual.
 */
export const AppStore = signalStore(
  { providedIn: 'root' },
  withState<{ theme: Theme }>({ theme: 'system' }),
  withComputed(({ theme }) => ({
    /**
     * Siguiente tema al que se transicionará al alternar el modo actual.
     */
    nextTheme: computed(() => nextTheme[theme()]),
  })),
  withMethods((store) => ({
    /**
     * Alterna el tema visual actual siguiendo el ciclo establecido.
     */
    toggleTheme(): void {
      patchState(store, ({ theme }) => ({ theme: nextTheme[theme] }));
    },
  })),
  withRegistrySlot(GlobalStore, 'app'),
);
