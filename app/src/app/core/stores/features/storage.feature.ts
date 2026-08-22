import { effect } from '@angular/core';
import { getState, patchState, signalStoreFeature, withHooks } from '@ngrx/signals';

export function withStorage<T>(storage: Storage, key: string) {
  return signalStoreFeature(
    withHooks((store) => {
      return {
        onInit() {
          const value = storage.getItem(key);

          if (value) {
            try {
              patchState(store, JSON.parse(value));
            } catch (e) {
              console.error(`Error parsing storage item '${key}' with value '${value}'`, e);
            }
          }

          effect(() => {
            storage.setItem(key, JSON.stringify(getState(store)));
          });
        },
      };
    }),
  );
}
