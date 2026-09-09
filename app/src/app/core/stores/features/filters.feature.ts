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

/**
 * Feature que añade la funcionalidad de utilización de filtros a un store.
 *
 * @param filters Estado inicial de los filtros.
 * @param countableFilters Nombres de los filtros contables (por ejemplo, aquellos ocultos en el badge de un icono).
 */
export function withFilters<T extends object>(filters: T, countableFilters: (keyof T)[] = []) {
  return signalStoreFeature(
    withState(filters),

    withComputed((store) => ({
      /**
       * Propiedad computada que cuenta los filtros activos y marcados como contables.
       */
      activeFiltersCount: computed(() => {
        return countableFilters.reduce((count, filter) => {
          const signals = store as Record<keyof T, Signal<unknown>>;
          return count + Number(isFilterActive(signals[filter](), filters[filter]));
        }, 0);
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
