import { computed, inject, Signal } from '@angular/core';
import { toPascalCase } from '@core/utils/string.utils';
import {
  patchState,
  signalStoreFeature,
  withComputed,
  withFeature,
  withMethods,
  withState,
} from '@ngrx/signals';
import { DatasetState, DatasetStore } from '../dataset.store';
import { SelectionState, withSelection } from '../selection.store';

/**
 * Determina si un filtro se encuentra activo comparando su valor actual con su valor inicial.
 *
 * @template T Tipo del filtro.
 * @param currentValue Valor actual del filtro.
 * @param initialValue Valor inicial del filtro.
 * @returns `true` si el filtro es diferente a su valor inicial.
 */
function isFilterActive<T>(currentValue: T, initialValue: T): boolean {
  /*
   * La comparación relajada '!=' contra 'null' verifica tanto 'null' como 'undefined',
   * al contrario de la comparación estricta '!==', que tambien verifica el tipo.
   */
  if (currentValue != null) {
    // Si es un array se debe comparar elemento a elemento
    if (Array.isArray(currentValue) && Array.isArray(initialValue)) {
      return (
        currentValue.length !== initialValue.length ||
        currentValue.some((value, index) => value !== initialValue[index])
      );
    }

    return currentValue !== initialValue;
  }

  return false;
}

/**
 * Tipo que construye el nombre de los setters para los filtros, por ejemplo:
 *
 * ```ts
 * T = { term: string; }
 * Setters<T> = { setTerm(value: string): void; }
 * ```
 *
 * @template T Tipo que contiene los filtros.
 */
type Setters<T> = {
  [K in keyof T & string as `set${Capitalize<K>}`]: (value: T[K]) => void;
};

/**
 * Feature que instancializa una colección de dominio vinculada al DatasetStore
 * y al SelectionStore.
 *
 * @template Item Tipo del elemento final que se lista y filtra.
 * @template Id Tipo del valor mapeado que se almacena en la selección.
 * @template Filters Tipo del objeto de filtros en crudo.
 * @template ComputedFilters Tipo del objeto de filtros transformados.
 */
export function withCollection<
  Item,
  Id,
  SelectionKey extends keyof SelectionState,
  Filters extends object = {},
  ComputedFilters extends object = Filters,
>(config: {
  selectionKey: SelectionKey;

  selector: (dataset: DatasetState) => Item[];
  identifier?: (item: Item) => Id;

  searchBy?: (item: Item) => string | null | undefined;

  filters?: Filters;
  computationFn?: (filters: Filters) => ComputedFilters;
  filterFn?: (filters: ComputedFilters, item: Item) => boolean;
  sortFn?: (a: Item, b: Item) => number;
  countableFilters?: (keyof Filters)[];
}) {
  type Searchable = { term: Signal<string> };

  const customFilters = config.filters ?? ({} as Filters);
  const customKeys = Object.keys(customFilters);

  const allFilters = { ...customFilters, term: '' };
  const allKeys = Object.keys(allFilters);

  return signalStoreFeature(
    /*
     * Extrae los items seleccionados del dataset.
     */
    withComputed(() => {
      const { data } = inject(DatasetStore);

      return {
        items: computed(() => config.selector(data.value())),
      };
    }),

    /*
     * Filtros (el término de búsqueda es común a todas las colecciones).
     */
    withState(allFilters),

    withComputed((store) => ({
      /**
       * Obtiene los elementos filtrados tras aplicar las configuraciones.
       */
      filteredItems: computed(() => {
        const signals = store as unknown as Searchable & Record<string, Signal<unknown>>;

        // Procesamiento del término de búsqueda
        const searchTerm = signals.term().trim().toLowerCase();
        const searchableElement = (item: Item) => config.searchBy?.(item) ?? (item as string);

        // Se filtran los elementos por término de búsqueda
        let items = store.items().filter((item) => {
          return searchableElement(item).includes(searchTerm);
        });

        // Se obtienen los valores crudos de los filtros
        const rawFilters = Object.fromEntries(
          customKeys.map((filter) => [filter, signals[filter]()]),
        );

        // Se aplican las transformaciones sobre los filtros
        const computedFilters =
          config.computationFn?.(rawFilters as Filters) ??
          (rawFilters as unknown as ComputedFilters);

        // Se filtran los elementos
        const filterFn = config.filterFn;
        items = filterFn
          ? store.items().filter((item) => filterFn(computedFilters, item))
          : store.items();

        // Se ordenan los elementos
        const sortFn = config.sortFn;

        if (sortFn) {
          items.sort(sortFn);
        } else {
          items.sort((a, b) => searchableElement(a).localeCompare(searchableElement(b)));
        }

        // Finalmente, se retornan los elementos ya procesados
        return items;
      }),

      /**
       * Cuenta los filtros activos y marcados como contables.
       */
      activeFiltersCount: computed(() => {
        return (
          config.countableFilters?.reduce((count, filter) => {
            const signals = store as Record<keyof Filters, Signal<unknown>>;
            return count + Number(isFilterActive(signals[filter](), customFilters[filter]));
          }, 0) ?? 0
        );
      }),
    })),

    withMethods((store) => ({
      cleanFilters(): void {
        patchState(store, customFilters);
      },
    })),

    withMethods(
      (store) =>
        /**
         * Construcción de los setters de los filtros.
         */
        Object.fromEntries(
          allKeys.map((filter) => [
            `set${toPascalCase(filter)}`,
            (value: unknown): void => patchState(store, { [filter]: value } as any),
          ]),
        ) as Setters<typeof allFilters>,
    ),

    withFeature(({ filteredItems }) =>
      withSelection(config.selectionKey, filteredItems, config.identifier),
    ),
  );
}
