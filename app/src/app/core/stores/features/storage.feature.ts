/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { signal, Signal } from '@angular/core';
import { patchState, signalStoreFeature, watchState, withHooks } from '@ngrx/signals';

/**
 * Feature que permite persistir un store completo o parcialmente en un Storage.
 *
 * @param storage Instancia del Storage (sessionStorage, localStorage).
 * @param keySignal Señal que computa la clave de almacenamiento.
 * @param keys Array opcional de propiedades del estado a persistir.
 */
export function withSignalStorage<T>(
  storage: Storage,
  keySignal: Signal<string | null>,
  keys?: (keyof T)[],
) {
  return signalStoreFeature(
    withHooks((store) => ({
      /**
       * Realiza la hidratación si hay datos compatibles en el storage
       * durante el montaje del store, normalmente a la vez que su componente.
       */
      onInit() {
        const key = keySignal();
        const item = key && storage.getItem(key);

        if (item) {
          try {
            patchState(store, JSON.parse(item));
          } catch (e) {
            console.error(`ERROR: Can not parse storage key: '${key}'`, item, e);
          }
        }

        watchState(store, (state) => {
          console.log(`State changed: '${JSON.stringify(state)}'`);
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
 * Feature que permite persistir un store completo o parcialmente en un Storage.
 *
 * @param storage Instancia del Storage (sessionStorage, localStorage, ...).
 * @param key Clave de almacenamiento.
 * @param keys Array opcional de propiedades del estado a persistir.
 */
export function withStorage<T>(storage: Storage, key: string, keys?: (keyof T)[]) {
  return withSignalStorage(storage, signal(key), keys);
}
