/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, Signal } from '@angular/core';
import { toPascalCase } from '@core/utils/string.utils';
import {
  patchState,
  signalStoreFeature,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';

/**
 * Determina si un filtro está activo.
 *
 * @param currentValue Valor actual del filtro
 * @param initialValue Valor inicial del filtro
 */
function isFilterActive<T>(currentValue: T, initialValue: T): boolean {
  if (currentValue != null) {
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
 * Tipo para la inferencia de los setters de los filtros.
 */
type Setters<T> = {
  [K in keyof T & string as `set${Capitalize<K>}`]: (values: T[K]) => void;
};

type WithFiltersOptions<I, F, T = F> = {
  filters: F;
  items: Signal<I[]>;
  filterFn: (filters: T, item: I) => boolean;
  transformFn?: (filters: F) => T;
  countableFilters?: (keyof F)[];
  sortFn?: (a: I, b: I) => number;
};

/**
 * Feature que añade la funcionalidad de utilización de filtros a un store.
 *
 * @param filters Estado inicial de los filtros.
 * @param countableFilters Nombres de los filtros contables (por ejemplo, aquellos ocultos en el badge de un icono).
 */
export function withFilters<I, F extends object, T = F>({
  filters,
  items,
  filterFn,
  transformFn,
  countableFilters,
  sortFn,
}: WithFiltersOptions<I, F, T>) {
  return signalStoreFeature(
    withState(filters),

    withComputed((store) => ({
      filteredItems: computed(() => {
        const signals = store as Record<string, Signal<unknown>>;

        const rawFilters = Object.fromEntries(
          Object.keys(filters).map((filter) => [filter, signals[filter]()]),
        ) as F;

        const computedFilters = transformFn
          ? transformFn(rawFilters)
          : (rawFilters as unknown as T);

        const filtered = items().filter((item) => filterFn(computedFilters, item));

        filtered.sort(sortFn);

        return filtered;
      }),

      /**
       * Propiedad computada que cuenta los filtros activos y marcados como contables.
       */
      activeFiltersCount: computed(() => {
        return (
          countableFilters?.reduce((count, filter) => {
            const signals = store as Record<keyof F, Signal<unknown>>;
            return count + Number(isFilterActive(signals[filter](), filters[filter]));
          }, 0) ?? 0
        );
      }),
    })),

    withMethods(
      (store) =>
        /**
         * Construcción de los setters de los filtros.
         */
        Object.fromEntries(
          Object.keys(filters).map((filter) => [
            `set${toPascalCase(filter)}`,
            (value: unknown): void => patchState(store, { [filter]: value } as any),
          ]),
        ) as Setters<T>,
    ),

    withMethods((store) => ({
      /**
       * Reinicializa los filtros.
       */
      clearFilters(): void {
        patchState(store, filters);
      },
    })),
  );
}
