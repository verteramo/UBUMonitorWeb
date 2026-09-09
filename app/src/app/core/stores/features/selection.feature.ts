/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, Signal } from '@angular/core';
import {
  patchState,
  signalStoreFeature,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';

/**
 * Propiedades de estado de la selección.
 */
type SelectionState<T> = {
  selection: T[];
};

/**
 * Feature que añade la funcionalidad de selección de elementos a un store.
 *
 * @param items Colección de ítems que gestionará la selección.
 */
export function withSelection<T>(items: Signal<T[]>) {
  /**
   * Estado inicial.
   */
  const initialState: SelectionState<T> = {
    selection: [],
  };

  return signalStoreFeature(
    withState(initialState),
    withComputed(({ selection }) => ({
      /**
       * Conjunto de la selección.
       * Proporciona el método `Set.prototype.has` con acceso directo O(1),
       * en lugar del método `Array.prototype.includes` con acceso lineal O(n).
       */
      _set: computed(() => new Set(selection())),
    })),
    withComputed(({ _set }) => ({
      /**
       * Determina si todos los ítems están seleccionados.
       */
      isCompleteSelection: computed(() => {
        return items().length > 0 && items().every((e) => _set().has(e));
      }),
    })),
    withComputed(({ _set, isCompleteSelection }) => ({
      /**
       * Determina si alguno de los ítems está seleccionado, pero no todos.
       */
      isPartialSelection: computed(() => {
        return !isCompleteSelection() && items().length > 0 && items().some((e) => _set().has(e));
      }),
    })),
    withMethods(({ _set }) => ({
      /**
       * Determina si un ítem particular está seleccionado.
       */
      isSelected(item: T): boolean {
        return _set().has(item);
      },
    })),
    withMethods((store) => ({
      /**
       * Cambia el estado de selección de un ítem determinado.
       */
      toggleItem(item: T): void {
        if (store.isSelected(item)) {
          patchState(store, ({ selection }) => ({
            selection: selection.filter((current) => current !== item),
          }));
        } else {
          patchState(store, ({ selection }) => ({
            selection: [...selection, item],
          }));
        }
      },

      /**
       * Cambia el estado de selección de todos los ítems.
       */
      toggleItems(): void {
        if (items().length > 0) {
          if (store.isCompleteSelection()) {
            const itemsSet = new Set(items());

            patchState(store, ({ selection }) => ({
              selection: selection.filter((current) => !itemsSet.has(current)),
            }));
          } else {
            patchState(store, ({ selection }) => ({
              selection: [...selection, ...items().filter((current) => !store._set().has(current))],
            }));
          }
        }
      },

      /**
       * Reinicializa la selección.
       */
      clearSelection(): void {
        patchState(store, { selection: [] });
      },
    })),
  );
}
