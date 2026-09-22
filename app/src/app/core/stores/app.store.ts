import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withSlice } from './features/slices.feature';
import { LocalSettingsStore } from './local-settings.store';

type Theme = 'system' | 'light' | 'dark';

const nextTheme: Record<Theme, Theme> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};

export const AppStore = signalStore(
  { providedIn: 'root' },
  withState<{ theme: Theme }>({ theme: 'system' }),
  withSlice(LocalSettingsStore, 'app'),
  withComputed(({ theme }) => ({
    nextTheme: computed(() => nextTheme[theme()]),
  })),
  withMethods((store) => ({
    toggleTheme(): void {
      patchState(store, ({ theme }) => ({ theme: nextTheme[theme] }));
    },
  })),
);
