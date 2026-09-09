/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

/*
 * En este fichero se define el estado global de la aplicación, que almacena
 * preferencias que no dependen de que un usuario haya iniciado sesión, como
 * por ejemplo las preferencias del formulario de login o el theme global.
 */

/**
 * Propiedades de estado de la aplicación.
 */
type AppState = {
  theme: 'system' | 'light' | 'dark';
  login: {
    host: string;
    hosts: string[];
    username: string;
    usernames: string[];
    offlineMode: boolean;
    rememberHost: boolean;
    rememberUsername: boolean;
  };
};

/**
 * Estado inicial.
 */
export const initialState: AppState = {
  theme: 'system',
  login: {
    host: '',
    hosts: [],
    rememberHost: false,
    username: '',
    usernames: [],
    rememberUsername: false,
    offlineMode: false,
  },
};

/**
 * Ciclo de themes que indica cuál es el siguiente.
 */
const nextTheme: Record<AppState['theme'], AppState['theme']> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};

/**
 * Store de propiedades de estado de la aplicación (no ligadas a sesión).
 */
export const AppStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(({ theme }) => ({
    /**
     * Theme siguiente.
     */
    nextTheme: computed(() => nextTheme[theme()]),
  })),
  withMethods((store) => ({
    /**
     * Cambia los themes en ciclo.
     */
    toggleTheme(): void {
      patchState(store, ({ theme }) => ({ theme: nextTheme[theme] }));
    },

    /**
     * Establece las preferencias seleccionadas por el usuario,
     * si se opta por recordar el host, también se añade a la lista de hosts conocidos.
     *
     * @param state Preferencias del usuario.
     */
    setLoginState(state: AppState['login']): void {
      const { host, hosts, rememberHost, username, usernames, rememberUsername } = state;

      patchState(store, {
        login: {
          ...state,
          host: rememberHost ? host : '',
          hosts: rememberHost ? [...new Set([...hosts, host])] : hosts,
          username: rememberUsername ? username : '',
          usernames: rememberUsername ? [...new Set([...usernames, username])] : usernames,
        },
      });
    },
  })),
  withStorage(localStorage, 'app'),
);
