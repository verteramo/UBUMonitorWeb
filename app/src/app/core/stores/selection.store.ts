import { computed, inject, Signal } from '@angular/core';
import { withRegistrySlot } from '@core/stores/features/registry.feature';
import {
  patchState,
  signalStore,
  signalStoreFeature,
  withComputed,
  withMethods,
  withProps,
  withState
} from '@ngrx/signals';
import { ContextStore } from './context.store';

/**
 * Estado global de las selecciones de la aplicación.
 */
export type SelectionState = {
  participants: number[];
  components: string[];
  events: string[];
  sections: number[];
  modules: number[];
  grades: number[];
  completions: number[];
};

/**
 * Estado inicial.
 */
const initialState: SelectionState = {
  participants: [],
  components: [],
  events: [],
  sections: [],
  modules: [],
  grades: [],
  completions: [],
};

/**
 * Store global para gestionar y sincronizar las selecciones.
 */
export const SelectionStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withRegistrySlot(ContextStore, 'selection'),
);

/**
 * Feature para conectar un store local con una clave del `SelectionStore` global.
 *
 * @template Item Tipo de los elementos de la colección de entrada.
 * @template Id Tipo de los elementos mapeados en la selección.
 * @param key Clave del estado de selección que se va a gestionar.
 * @param collection Señal con la colección de elementos origen.
 * @param mapFn Función para transformar cada elemento al tipo de selección.
 */
export function withSelection<Item, Id = Item>(
  key: keyof SelectionState,
  collection: Signal<Item[]>,
  mapFn?: (value: Item) => Id,
) {
  const element = (item: Item): Id => mapFn?.(item) ?? (item as unknown as Id);

  const items = computed(() =>
    mapFn ? collection().map(mapFn) : (collection() as unknown as Id[]),
  );

  return signalStoreFeature(
    withProps(() => {
      const store = inject(SelectionStore);

      return {
        selection: store[key] as Signal<Id[]>,
      };
    }),
    withComputed(({ selection }) => ({
      /**
       * Conjunto optimizado en O(1) para comprobar elementos seleccionados.
       */
      selectionSet: computed(() => new Set(selection())),
    })),
    withComputed(({ selectionSet }) => ({
      /**
       * Indica si todos los elementos están seleccionados.
       */
      isCompleteSelection: computed(() => {
        return items().length > 0 && items().every((e) => selectionSet().has(e));
      }),
    })),
    withComputed(({ selectionSet, isCompleteSelection }) => ({
      /**
       * Indica si hay una selección parcial (algunos pero no todos).
       */
      isPartialSelection: computed(() => {
        return (
          !isCompleteSelection() && items().length > 0 && items().some((e) => selectionSet().has(e))
        );
      }),
    })),
    withMethods(({ selectionSet }) => ({
      /**
       * Comprueba si un elemento individual está seleccionado.
       *
       * @param item Elemento que se desea comprobar.
       * @returns `true` si el elemento está seleccionado; de lo contrario, `false`.
       */
      isSelected(item: Item): boolean {
        return selectionSet().has(element(item));
      },
    })),
    withMethods((store) => {
      const source = inject(SelectionStore);

      return {
        /**
         * Alterna la selección de un elemento específico.
         *
         * @param item Elemento cuyo estado de selección se quiere cambiar.
         */
        toggleItem(item: Item): void {
          const current = source[key]() as Id[];
          const el = element(item);

          if (store.isSelected(item)) {
            patchState(source as any, { [key]: current.filter((e) => e !== el) });
          } else {
            patchState(source as any, {
              [key]: [...current, el],
            });
          }
        },

        /**
         * Alterna la selección de todos los elementos disponibles.
         */
        toggleItems(): void {
          if (items().length > 0) {
            const current = source[key]() as Id[];

            if (store.isCompleteSelection()) {
              const itemsSet = new Set(items());

              patchState(source as any, {
                [key]: current.filter((curr) => !itemsSet.has(curr)),
              });
            } else {
              const missing = items().filter((curr) => !store.selectionSet().has(curr));

              patchState(source as any, {
                [key]: [...current, ...missing],
              });
            }
          }
        },

        /**
         * Limpia la selección actual.
         */
        clearSelection(): void {
          patchState(source as any, { [key]: [] });
        },
      };
    }),
  );
}
