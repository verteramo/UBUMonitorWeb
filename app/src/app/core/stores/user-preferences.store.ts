/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { nextTheme } from '@core/models/theme';
import { UserPreferences } from '@core/models/user-preferences';
import { SessionService } from '@core/services/session.service';
import { toPascalCase } from '@core/utils/string.utils';
import {
  patchState,
  signalStore,
  withComputed,
  withFeature,
  withMethods,
  withState,
} from '@ngrx/signals';
import { withAutoSave } from './features/autosave.feature';
import { extractLoadingFromState, withLoading } from './features/loading.feature';

type PreferenceSetters = {
  [K in keyof UserPreferences & string as `set${Capitalize<K>}`]: (
    value: UserPreferences[K],
  ) => void;
};

const initialState: UserPreferences = {
  theme: 'system',
  language: 'en',
  autosaveDelayMs: 5000,
};

const keys = Object.keys(initialState) as (keyof UserPreferences)[];

export const UserPreferencesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withLoading(),
  withComputed(({ theme }) => ({
    nextTheme: computed(() => nextTheme[theme()]),
  })),
  withFeature((store) =>
    withAutoSave(
      () => {
        const sessionService = inject(SessionService);
        return (prefs: UserPreferences) => sessionService.putPreferences(prefs);
      },
      () => store.autosaveDelayMs(),
    ),
  ),
  withMethods((store) => ({
    /**
     * Establece las preferencias del usuario obtenidas desde el backend tras el login.
     */
    setPreferences(preferences: UserPreferences): void {
      patchState(store, { ...initialState, ...preferences });
    },

    toggleTheme(): void {
      patchState(store, (state) => {
        const updatedState = { ...state, theme: nextTheme[state.theme] };
        store.save(extractLoadingFromState(updatedState));
        return updatedState;
      });
    },
  })),
  withMethods(
    (store) =>
      Object.fromEntries(
        keys.map((key) => [
          `set${toPascalCase(String(key))}`,
          (value: unknown): void => {
            patchState(store, (state) => {
              const updatedState = { ...state, [key]: value };
              store.save(extractLoadingFromState(updatedState));
              return updatedState;
            });
          },
        ]),
      ) as PreferenceSetters,
  ),
);
