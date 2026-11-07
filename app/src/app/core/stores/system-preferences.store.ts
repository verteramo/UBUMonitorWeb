import { computed, inject } from '@angular/core';
import { SystemPreferences, Theme } from '@core/models/system-preferences';
import { SystemPreferencesService } from '@core/services/system-preferences.service';
import { cleanUrl } from '@core/utils/string.utils';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';

type SystemPreferencesState = SystemPreferences & {
  loading: boolean;
};

const initialState: SystemPreferencesState = {
  theme: 'system',
  sites: [],
  usernames: [],
  rememberSite: false,
  rememberUsername: false,
  loading: true,
};

const nextTheme: Record<Theme, Theme> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};

export const SystemPreferencesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ theme, sites, usernames }) => ({
    nextTheme: computed(() => nextTheme[theme()]),
    site: computed(() => sites()[0] ?? ''),
    username: computed(() => usernames()[0] ?? ''),
  })),
  withMethods((store, prefsService = inject(SystemPreferencesService)) => ({
    loadPreferences: rxMethod<void>(
      pipe(
        switchMap(() => prefsService.read()),
        tap((prefs) => {
          patchState(store, {
            ...prefs,
            loading: false,
          });
        }),
      ),
    ),

    toggleTheme(): void {
      patchState(store, (state) => {
        const updatedState = {
          ...state,
          theme: nextTheme[state.theme],
        };

        prefsService.write(updatedState).subscribe();
        return updatedState;
      });
    },

    updateSitePreferences(site: string, remember: boolean): void {
      patchState(store, (state) => {
        const updatedSites = remember
          ? [...new Set([cleanUrl(site), ...state.sites])]
          : state.sites;

        const updatedState = {
          ...state,
          rememberSite: remember,
          sites: updatedSites,
        };

        prefsService.write(updatedState).subscribe();
        return updatedState;
      });
    },

    updateUsernamePreferences(username: string, remember: boolean): void {
      patchState(store, (state) => {
        const updatedUsernames = remember
          ? [...new Set([username, ...state.usernames])]
          : state.usernames;

        const updatedState = {
          ...state,
          rememberUsername: remember,
          usernames: updatedUsernames,
        };

        prefsService.write(updatedState).subscribe();
        return updatedState;
      });
    },
  })),
);
