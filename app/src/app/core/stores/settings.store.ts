import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

export type ThemeMode = 'light' | 'dark' | 'system';

export interface SettingsState {
  theme: ThemeMode;
}

export const settingsInitialState: SettingsState = {
  theme: 'system',
};

export const SettingsStore = signalStore(
  { providedIn: 'root' },
  withState<SettingsState>(settingsInitialState),
  withStorage(localStorage, 'settings'),
  withMethods((store) => ({
    setTheme(theme: ThemeMode): void {
      patchState(store, { theme });
    },
    clear(): void {
      patchState(store, settingsInitialState);
    },
  })),
);
