/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { withRegistrySlot } from '@core/stores/features/registry.feature';
import { GlobalStore } from '@core/stores/global.store';
import { cleanUrl } from '@core/utils/string.utils';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';

/**
 * Preferencias del formulario de login.
 */
type LoginPreferencesState = {
  sites: string[];
  rememberSite: boolean;
  usernames: string[];
  rememberUsername: boolean;
};

/**
 * Estado inicial de las preferencias del formulario de login.
 */
const initialState: LoginPreferencesState = {
  sites: [],
  rememberSite: false,
  usernames: [],
  rememberUsername: false,
};

/**
 * Store de preferencias del formulario de login.
 */
export const LoginPreferencesStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ sites, usernames }) => ({
    /**
     * Último sitio recordado.
     */
    site: computed(() => sites()[0] ?? ''),

    /**
     * Último nombre de usuario recordado.
     */
    username: computed(() => usernames()[0] ?? ''),
  })),
  withMethods((store) => ({
    /**
     * Actualiza las preferencias del sitio.
     *
     * @param site URL del sitio Moodle.
     * @param remember Indica si se debe recordar y almacenar el sitio.
     */
    updateSitePreferences(site: string, remember: boolean): void {
      patchState(store, (state) => ({
        rememberSite: remember,
        sites: remember ? [...new Set([cleanUrl(site), ...state.sites])] : state.sites,
      }));
    },

    /**
     * Actualiza las preferencias del nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @param remember Indica si se debe recordar y almacenar el usuario.
     */
    updateUsernamePreferences(username: string, remember: boolean): void {
      patchState(store, (state) => ({
        rememberUsername: remember,
        usernames: remember ? [...new Set([username, ...state.usernames])] : state.usernames,
      }));
    },
  })),
  withRegistrySlot(GlobalStore, 'login'),
);
