import { computed, inject } from '@angular/core';
import { nextTheme } from '@core/models/theme';
import { UserPreferences } from '@core/models/user-prefereces';
import { SessionService } from '@core/services/session.service';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withAutoSave } from './features/auto-save.feature';
import { extractLoadingFromState, withLoading } from './features/loading.feature';
import { SessionStore } from './session.store';

const initialState: UserPreferences = {
  theme: 'system',
  language: 'en',
  autosaveDelayMs: 5000,
};

export const UserPreferencesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withLoading(),
  withComputed(({ theme }) => ({
    nextTheme: computed(() => nextTheme[theme()]),
  })),
  withAutoSave(() => {
    const sessionService = inject(SessionService);
    return (prefs: UserPreferences) => sessionService.putPreferences(prefs);
  }),
  withMethods((store, sessionStore = inject(SessionStore)) => {

    function syncUserPreferences(prefereces: UserPreferences): void {
      const user = sessionStore.user();

      if (user) {
        patchState(sessionStore, {
          user: {
            ...user,
            preferences: prefereces
          }
        })
      }
    }

    return {
      loadPreferences(): void {
        store.startLoading();

        const user = sessionStore.user();
        const preferences = (user?.preferences || {}) as Partial<UserPreferences>;

        patchState(store, {
          ...initialState,
          ...preferences,
        });

        store.stopLoading();
      },

      toggleTheme(): void {
        patchState(store, (state) => {
          const updatedState = { ...state, theme: nextTheme[state.theme] };
          store.save(extractLoadingFromState(updatedState));
          return updatedState;
        });
      },

      /**
       * Método genérico y seguro: TS te obligará a pasar una clave real (ej: 'theme')
       * y un valor válido para esa clave (ej: 'dark').
       */
      updatePreference<K extends keyof UserPreferences>(key: K, value: UserPreferences[K]): void {
        patchState(store, (state) => {
          const updatedState = { ...state, [key]: value };
          store.save(extractLoadingFromState(updatedState));
          return updatedState;
        });
      },
    };
  }),
);
