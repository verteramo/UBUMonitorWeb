export type Theme = 'system' | 'light' | 'dark';

export const nextTheme: Record<Theme, Theme> = {
  system: 'light',
  light: 'dark',
  dark: 'system',
};
