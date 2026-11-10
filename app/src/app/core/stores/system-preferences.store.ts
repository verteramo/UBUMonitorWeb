import { computed, inject } from '@angular/core';
import { SystemPreferences } from '@core/models/system-preferences';
import { nextTheme } from '@core/models/theme';
import { SystemService } from '@core/services/system.service';
import { cleanUrl } from '@core/utils/string.utils';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, EMPTY, pipe, switchMap, tap } from 'rxjs';
import { withAutoSave } from './features/auto-save.feature';
import { extractLoadingFromState, withLoading } from './features/loading.feature';

const initialState: SystemPreferences = {
  theme: 'system',
  sites: [],
  usernames: [],
  rememberSite: false,
  rememberUsername: false,
};

export const SystemPreferencesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withLoading(),
  withComputed(({ theme, sites, usernames }) => ({
    nextTheme: computed(() => nextTheme[theme()]),
    site: computed(() => sites()[0] ?? ''),
    username: computed(() => usernames()[0] ?? ''),
  })),
  withAutoSave(() => {
    const prefsService = inject(SystemService);
    return (prefs: SystemPreferences) => prefsService.putPreferences(prefs);
  }),
  withMethods((store, prefsService = inject(SystemService)) => ({
    loadPreferences: rxMethod<void>(
      pipe(
        tap(() => store.startLoading()),
        switchMap(() => prefsService.getPreferences()),
        tap((preferences) => {
          patchState(store, {
            ...initialState,
            ...preferences,
          });

          store.stopLoading();
        }),
        catchError(() => {
          store.stopLoading();
          return EMPTY;
        }),
      ),
    ),

    toggleTheme(): void {
      patchState(store, (state) => {
        const updatedState = {
          ...state,
          theme: nextTheme[state.theme],
        };

        store.save(extractLoadingFromState(updatedState));
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

        store.save(extractLoadingFromState(updatedState));
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

        store.save(extractLoadingFromState(updatedState));
        return updatedState;
      });
    },
  })),
);
