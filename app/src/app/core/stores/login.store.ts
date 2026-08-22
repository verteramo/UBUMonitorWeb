import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

export type LoginState = {
  host: string;
  username: string;
  rememberHost: boolean;
  rememberUsername: boolean;
  offlineMode: boolean;
  hosts: string[];
};

export const loginInitialState: LoginState = {
  host: '',
  username: '',
  rememberHost: false,
  rememberUsername: false,
  offlineMode: false,
  hosts: [],
};

export const LoginStore = signalStore(
  { providedIn: 'root' },
  withState(loginInitialState),
  withStorage(localStorage, 'login-state'),
  withMethods((store) => ({
    set(state: LoginState): void {
      patchState(store, {
        ...state,
        host: state.rememberHost ? state.host : '',
        username: state.rememberUsername ? state.username : '',
        hosts: [...new Set([...state.hosts, state.host])],
      });
    },

    update(updater: (state: LoginState) => LoginState): void {
      patchState(store, updater);
    },

    clear(): void {
      patchState(store, loginInitialState);
    },
  })),
);
