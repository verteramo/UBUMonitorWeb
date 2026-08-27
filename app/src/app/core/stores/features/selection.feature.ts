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
      selectionSet: computed(() => new Set(selection())),
      selectionLength: computed(() => selection().length),
    })),
    withMethods((store) => ({
      isSelected(item: T): boolean {
        return store.selection().includes(item);
      },

      toggleItem(item: T): void {
        const items = new Set(store.selection());

        if (items.has(item)) {
          items.delete(item);
        } else {
          items.add(item);
        }

        patchState(store, { selection: [...items] });
      },

      toggleItems(items: T[]): void {
        const selection = store.selection();
        const isAllSelected = items.length > 0 && items.every((item) => selection.includes(item));

        if (isAllSelected) {
          patchState(store, { selection: [] });
        } else {
          patchState(store, { selection: [...new Set(items)] });
        }
      },

      clearSelection(): void {
        patchState(store, { selection: [] });
      },
    })),
  );
}
