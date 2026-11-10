import { Theme } from './theme';

export type SystemPreferences = {
  theme: Theme;
  sites: string[];
  usernames: string[];
  rememberSite: boolean;
  rememberUsername: boolean;
};
