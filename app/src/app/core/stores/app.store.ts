/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

/*
 * En este fichero se define el estado global de la aplicación,
 * que almacena preferencias que no dependen de que un usuario
 * haya iniciado sesión, como por ejemplo las preferencias del
 * formulario de logín o el theme global.
 */

/** Propiedades de estado de la aplicación. */
type AppState = {
  theme: 'system' | 'light' | 'dark';
  login: {
    host: string;
    hosts: string[];
    username: string;
    offlineMode: boolean;
    rememberHost: boolean;
    rememberUsername: boolean;
  };
};

/** Estado inicial. */
const initialState: AppState = {
  theme: 'system',
  login: {
    host: '',
    hosts: [],
    username: '',
    offlineMode: false,
    rememberHost: false,
    rememberUsername: false,
  },
};

/** Ciclo de themes que indica cuál es el siguiente. */
const nextTheme: Record<AppState['theme'], AppState['theme']> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};

/** Store de propiedades de estado de la aplicación (no ligadas a sesión). */
export const AppStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withStorage(localStorage, 'app-state'),
  withComputed(({ theme }) => ({
    /** Theme siguiente. */
    nextTheme: computed(() => nextTheme[theme()]),
  })),
  withMethods((store) => ({
    /** Cambia los themes en ciclo. */
    toggleTheme(): void {
      patchState(store, { theme: store.nextTheme() });
    },

    /**
     * Establece las preferencias seleccionadas por el usuario,
     * si se opta por recordar el host, también se añade a la lista de hosts conocidos.
     *
     * @param state Preferencias del usuario.
     */
    setLoginState(state: AppState['login']): void {
      patchState(store, {
        login: {
          ...state,
          host: state.rememberHost ? state.host : '',
          username: state.rememberUsername ? state.username : '',
          hosts: state.rememberHost ? [...new Set([...state.hosts, state.host])] : state.hosts,
        },
      });
    },
  })),
);

export const initialLoginState = initialState.login;
