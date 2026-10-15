/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject, linkedSignal } from '@angular/core';
import { cleanUrl } from '@core/utils/string.utils';
import { signalStoreFeature, withComputed, withMethods, withProps } from '@ngrx/signals';
import { LoginPreferencesStore } from './login-prefs.store';

/**
 * Propiedades de estado del modelo del formulario de login.
 */
export type LoginModelState = {
  site: string;
  username: string;
  password: string;
  rememberSite: boolean;
  rememberUsername: boolean;
};

/**
 * Estado inicial del modelo del formulario de login.
 */
const initialState: LoginModelState = {
  site: '',
  username: '',
  password: '',
  rememberSite: false,
  rememberUsername: false,
};

// Feature o Store solo para el model:
export function withLoginModel() {
  return signalStoreFeature(
    withComputed(() => {
      const prefs = inject(LoginPreferencesStore);

      return {
        /**
         * Estado inicial con las preferencias cargadas.
         */
        loadedState: computed(() => ({
          ...initialState,
          site: prefs.site(),
          rememberSite: prefs.rememberSite(),
          username: prefs.username(),
          rememberUsername: prefs.rememberUsername(),
        })),
      };
    }),
    withProps(({ loadedState }) => ({
      /**
       * WritableSignal que sirve como modelo
       * subyacente para la señal del formulario de login.
       */
      model: linkedSignal(() => loadedState()),
    })),
    withComputed(({ model }) => {
      const prefs = inject(LoginPreferencesStore);
      const cleanSite = computed(() => cleanUrl(model().site.trim().toLowerCase()));
      const cleanUsername = computed(() => model().username.trim().toLowerCase());

      return {
        /**
         * Determina si la URL del sitio introducido utiliza el protocolo inseguro.
         */
        insecure: computed(() => cleanSite().startsWith('http:')),

        /**
         * Lista de URLs de los sitios filtrados de acuerdo con la entrada del usuario.
         */
        filteredSites: computed(() =>
          prefs.sites().filter((current) => current.toLowerCase().startsWith(cleanSite())),
        ),

        /**
         * Lista de nombres de usuarios filtrados de acuerdo con la entrada del usuario.
         */
        filteredUsernames: computed(() =>
          prefs.usernames().filter((current) => current.toLowerCase().startsWith(cleanUsername())),
        ),
      };
    }),
    withMethods(({ model, loadedState }) => {
      /**
       * Actualiza el estado del sitio.
       *
       * @param state Nuevo estado.
       */
      function updateSite({ site, rememberSite }: LoginModelState): void {
        model.update((state) => ({ ...state, site, rememberSite }));
      }

      /**
       * Actualiza el estado del nombre de usuario.
       *
       * @param state Nuevo estado.
       */
      function updateUsername({ username, rememberUsername }: LoginModelState): void {
        model.update((state) => ({ ...state, username, rememberUsername }));
      }

      return {
        /**
         * Reinicializa los campos relacionados con el sitio.
         */
        cleanSite(): void {
          updateSite(initialState);
        },

        /**
         * Restaura los campos relacionados con el sitio.
         */
        restoreSite(): void {
          updateSite(loadedState());
        },

        /**
         * Reinicializa los campos relacionados con el nombre de usuario.
         */
        cleanUsername(): void {
          updateUsername(initialState);
        },

        /**
         * Restaura los campos relacionados con el nombre de usuario.
         */
        restoreUsername(): void {
          updateUsername(loadedState());
        },
      };
    }),
  );
}
