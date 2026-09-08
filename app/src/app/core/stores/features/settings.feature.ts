import { inject } from '@angular/core';
import { getState, patchState, signalStoreFeature, watchState, withHooks } from '@ngrx/signals';
import { SettingsStore } from '../settings.store';

export function withSettings(key: string) {
  return signalStoreFeature(
    withHooks({
      onInit(store) {
        const settings = inject(SettingsStore);
        const initialState = getState(settings).stores[key];

        if (initialState) {
          patchState(store, initialState);
        }

        watchState(store, (state) => settings.setStoreState(key, state));
      },
    }),
  );
}
