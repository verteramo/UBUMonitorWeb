/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { inject } from '@angular/core';
import { getState, patchState, signalStoreFeature, watchState, withHooks } from '@ngrx/signals';
import { SettingsStore } from '../settings.store';

/**
 * Feature que permite reservar un slice dentro de la `SettingsStore`
 * para persistir el estado de un store.
 *
 * @param slice Slice dentro de la `SettingsStore`.
 */
export function withSettingsSlice(slice: string) {
  return signalStoreFeature(
    withHooks({
      onInit(store) {
        const settings = inject(SettingsStore);
        const initialState = getState(settings).stores[slice];

        if (initialState) {
          patchState(store, initialState);
        }

        watchState(store, (state) => settings.setStoreState(slice, state));
      },
    }),
  );
}
