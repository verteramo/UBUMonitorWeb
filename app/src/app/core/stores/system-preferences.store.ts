import { computed, inject } from '@angular/core';
import { SystemPreferences } from '@core/models/system-preferences';
import { nextTheme } from '@core/models/theme';
import { SystemService } from '@core/services/system.service';
import { cleanUrl } from '@core/utils/string.utils';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, EMPTY, finalize, pipe, switchMap, tap } from 'rxjs';
import { withLoading } from './features/loading.feature';

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
  withMethods((store, systemService = inject(SystemService)) => ({
    loadPreferences: rxMethod<void>(
      pipe(
        tap(() => store.startLoading()),
        switchMap(() =>
          systemService.getPreferences().pipe(
            tap((preferences) => patchState(store, { ...preferences })),
            catchError((e) => {
              console.error('Error loading system preferences', e);
              return EMPTY;
            }),
            finalize(() => store.stopLoading()),
          ),
        ),
      ),
    ),

    toggleTheme(): void {
      patchState(store, (state) => {
        const updatedState = { ...state, theme: nextTheme[state.theme] };
        systemService.putPreferences(updatedState).subscribe();
        return updatedState;
      });
    },

    updateSitePreferences(site: string, rememberSite: boolean): void {
      patchState(store, (state) => {
        const sites = rememberSite ? [...new Set([cleanUrl(site), ...state.sites])] : state.sites;

        const updatedState = { ...state, rememberSite, sites };
        systemService.putPreferences(updatedState).subscribe();
        return updatedState;
      });
    },

    updateUsernamePreferences(username: string, rememberUsername: boolean): void {
      patchState(store, (state) => {
        const usernames = rememberUsername
          ? [...new Set([username, ...state.usernames])]
          : state.usernames;

        const updatedState = { ...state, rememberUsername, usernames };
        systemService.putPreferences(updatedState).subscribe();
        return updatedState;
      });
    },
  })),
);
