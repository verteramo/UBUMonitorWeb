import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

export type LoginFormState = {
  host: string;
  username: string;
  rememberHost: boolean;
  rememberUsername: boolean;
  offlineMode: boolean;
  hosts: string[];
};

export const loginFormInitialState: LoginFormState = {
  host: '',
  username: '',
  rememberHost: false,
  rememberUsername: false,
  offlineMode: false,
  hosts: [],
};

export const LoginFormStore = signalStore(
  withState(loginFormInitialState),
  withStorage(localStorage, 'login'),
  withMethods((store) => ({
    set: (state: LoginFormState) => {
      patchState(store, {
        ...state,
        host: state.rememberHost ? state.host : '',
        username: state.rememberUsername ? state.username : '',
        hosts: [...new Set([...state.hosts, state.host])],
      });
    },

    clear: () => {
      patchState(store, loginFormInitialState);
    },
  })),
);
