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
 * @param key Nombre o factoría de la clave de almacenamiento.
 * @returns Feature.
 */
export function withStorage(storage: Storage, key: string | (() => Signal<string | null>)) {
  return signalStoreFeature(
    withHooks((store) => {
      // Obtención de la signal de la clave de almacenamiento
      const keySignal = typeof key === 'function' ? key() : signal(key);

      return {
        onInit() {
          const key = keySignal();

          // Caga inicial del estado de la store desde el storage, si existe
          if (key) {
            const item = storage.getItem(key);

            if (item) {
              try {
                patchState(store, JSON.parse(item));
              } catch (e) {
                console.error(`Error parsing storage key '${key}'`, item, e);
              }
            }
          }

          // Se dispara en cada cambio de clave para almacenar
          // el estado de la store en el storage
          watchState(store, (state) => {
            const key = keySignal();

            if (key) {
              storage.setItem(key, JSON.stringify(state));
            }
          });
        },
      };
    }),
  );
}
