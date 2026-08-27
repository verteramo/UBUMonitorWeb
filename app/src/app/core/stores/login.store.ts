/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { patchState, signalStore, withMethods, withProps, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

/** Propiedades de estado del formulario de login. */
type LoginState = {
  /** Host Moodle. */
  host: string;

  /** Nombre de usuario. */
  username: string;

  /** Flag para guardar el host. */
  rememberHost: boolean;

  /** Flag para guardar el nombre de usuario. */
  rememberUsername: boolean;

  /** Flag para modo offline. */
  offlineMode: boolean;

  /** Lista de hosts conocidos. */
  hosts: string[];
};

/** Estado inicial del formulario de login. */
const initialState: LoginState = {
  host: '',
  username: '',
  rememberHost: false,
  rememberUsername: false,
  offlineMode: false,
  hosts: [],
};

/** Store de propiedades de estado del formulario de login. */
export const LoginStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withProps(() => ({ initialState })),
  withStorage(localStorage, 'login-state'),
  withMethods((store) => ({
    /**
     * Establece las preferencias seleccionadas por el usuario,
     * si se opta por recordar el host, también se añade a la lista de hosts conocidos.
     *
     * @param state Preferencias del usuario.
     */
    set(state: LoginState): void {
      patchState(store, {
        ...state,
        host: state.rememberHost ? state.host : '',
        username: state.rememberUsername ? state.username : '',
        hosts: state.rememberHost ? [...new Set([...state.hosts, state.host])] : state.hosts,
      });
    },
  })),
);
