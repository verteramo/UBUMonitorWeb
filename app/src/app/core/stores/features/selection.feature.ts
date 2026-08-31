/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import {
  patchState,
  signalStoreFeature,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';

/**
 * Feature que añade la funcionalidad de selección de elementos a un store.
 *
 * @returns Feature.
 */
export function withSelection<T>() {
  return signalStoreFeature(
    withState<{ selection: T[] }>({ selection: [] }),
    withComputed(({ selection }) => ({
      _selectionSet: computed(() => new Set(selection())),
    })),
    withMethods((store) => ({
      isSelected(item: T): boolean {
        return store._selectionSet().has(item);
      },

      toggleItem(item: T): void {
        const set = new Set(store._selectionSet());

        if (set.has(item)) {
          set.delete(item);
        } else {
          set.add(item);
        }

        patchState(store, { selection: [...set] });
      },

      toggleItems(items: T[]): void {
        const set = new Set(store._selectionSet());
        const isAllSelected = items.length > 0 && items.every((item) => set.has(item));

        if (isAllSelected) {
          items.forEach((item) => set.delete(item));
        } else {
          items.forEach((item) => set.add(item));
        }

        patchState(store, { selection: [...set] });
      },

      clearSelection(): void {
        patchState(store, { selection: [] });
      },
    })),
  );
}
