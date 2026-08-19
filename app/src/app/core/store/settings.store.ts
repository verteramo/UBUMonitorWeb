import { inject, Injectable } from '@angular/core';
import { LOCAL_STORAGE } from '@core/services/storage.service';
import { AbstractStore } from './abstract-store';

export type ThemeMode = 'light' | 'dark' | 'system';

export interface Settings {
  theme: ThemeMode;
}

@Injectable({ providedIn: 'root' })
export class SettingsStore extends AbstractStore<Settings> {
  constructor() {
    super('settings', inject(LOCAL_STORAGE), {
      theme: 'system',
    });
  }

  setTheme(theme: ThemeMode) {
    this.update((state) => ({ ...state, theme }));
  }
}
