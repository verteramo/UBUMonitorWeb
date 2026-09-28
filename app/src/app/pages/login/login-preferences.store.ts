/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { withSlice } from '@core/stores/features/slices.feature';
import { LocalSettingsStore } from '@core/stores/local-settings.store';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';

/**
 * Preferencias del formulario de login.
 */
type LoginPreferencesState = {
  hosts: string[];
  rememberHost: boolean;
  usernames: string[];
  rememberUsername: boolean;
};

/**
 * Estado inicial de las preferencias del formulario de login.
 */
const initialState: LoginPreferencesState = {
  hosts: [],
  rememberHost: false,
  usernames: [],
  rememberUsername: false,
};

/**
 * Store de preferencias del formulario de login.
 */
export const LoginPreferencesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ hosts, usernames }) => ({
    host: computed(() => hosts()[0] ?? ''),
    username: computed(() => usernames()[0] ?? ''),
  })),
  withMethods((store) => ({
    setHostPreferences(host: string, rememberHost: boolean): void {
      patchState(store, (state) => ({
        rememberHost,
        hosts: rememberHost ? [...new Set([host, ...state.hosts])] : state.hosts,
      }));
    },

    saveUsernamePreferences(username: string, rememberUsername: boolean): void {
      patchState(store, (state) => ({
        rememberUsername,
        usernames: rememberUsername
          ? [...new Set([username, ...state.usernames])]
          : state.usernames,
      }));
    },
  })),
  withSlice(LocalSettingsStore, 'login'),
);
