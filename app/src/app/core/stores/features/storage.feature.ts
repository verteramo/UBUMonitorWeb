/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { signal, Signal } from '@angular/core';
import { patchState, signalStoreFeature, watchState, withHooks } from '@ngrx/signals';

/**
 * Feature que permite persistir un store completo en un Storage.
 *
 * @param storage Instancia del Storage (sessionStorage, localStorage, ...).
 * @param keySignal Señal que computa la clave de almacenamiento.
 * @returns Feature.
 */
export function withSignalStorage(storage: Storage, keySignal: Signal<string | null>) {
  return signalStoreFeature(
    withHooks((store) => ({
      onInit() {
        const key = keySignal();
        const item = key && storage.getItem(key);

        if (item) {
          try {
            patchState(store, JSON.parse(item));
          } catch (e) {
            console.error(`Error parsing storage key '${key}'`, item, e);
          }
        }

        watchState(store, (state) => {
          const key = keySignal();

          if (key) {
            storage.setItem(key, JSON.stringify(state));
          }
        });
      },
    })),
  );
}

/**
 * Feature que permite persistir un store completo en un Storage.
 *
 * @param storage Instancia del Storage (sessionStorage, localStorage, ...).
 * @param key Clave de almacenamiento.
 * @returns Feature.
 */
export function withStorage(storage: Storage, key: string) {
  return withSignalStorage(storage, signal(key));
}
