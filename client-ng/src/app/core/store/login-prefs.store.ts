import { inject, Service } from '@angular/core';
import { LOCAL_STORAGE } from '@core/services/storage.service';
import { AbstractStore } from './abstract-store';

export interface LoginPrefs {
  host: string;
  username: string;
  rememberHost: boolean;
  rememberUsername: boolean;
  offlineMode: boolean;
  hosts: string[];
}

@Service()
export class LoginPrefsStore extends AbstractStore<LoginPrefs> {
  constructor() {
    super('login-prefs', inject(LOCAL_STORAGE), {
      host: '',
      username: '',
      rememberHost: false,
      rememberUsername: false,
      offlineMode: false,
      hosts: [],
    });
  }

  protected override reduce(oldValue: LoginPrefs, newValue: LoginPrefs): LoginPrefs {
    const { host } = newValue;
    const { hosts } = oldValue;

    return {
      host: newValue.rememberHost ? newValue.host : '',
      username: newValue.rememberUsername ? newValue.username : '',
      rememberHost: newValue.rememberHost,
      rememberUsername: newValue.rememberUsername,
      offlineMode: newValue.offlineMode,
      hosts: hosts.includes(host) ? hosts : [...hosts, host],
    };
  }
}
