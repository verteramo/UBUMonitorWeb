export interface LoginPreferences {
  host: string;
  username: string;
  rememberHost: boolean;
  rememberUser: boolean;
  hosts: string[];
}

export const PREFERENCES_KEY = 'login-preferences';
