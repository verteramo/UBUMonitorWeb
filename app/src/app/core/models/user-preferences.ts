import { Theme } from './theme';

export type UserPreferences = {
  theme: Theme;
  language: string;
  autosaveDelayMs: number;
};
