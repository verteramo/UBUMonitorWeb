export type Theme = 'system' | 'light' | 'dark';

export type SystemPreferences = {
  theme: Theme;
  sites: string[];
  usernames: string[];
  rememberSite: boolean;
  rememberUsername: boolean;
};
