/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject, Signal } from '@angular/core';
import { signalStoreFeature, withProps } from '@ngrx/signals';
import { DatasetState, DatasetStore } from '../dataset.store';

/**
 * Tipo para inferir el tipo de las señales de los slices y de `isLoading`.
 */
type Slices<T extends keyof DatasetState> = {
  [K in T]: Signal<DatasetState[K]>;
} & {
  isLoading: Signal<boolean>;
};

/**
 * Feature que permite suscribirse a slices del `DatasetStore`.
 *
 * @param slices Slices a los que se desea suscribir.
 */
export function withDatasetSlice<T extends keyof DatasetState>(...slices: T[]) {
  return signalStoreFeature(
    withProps(
      (_, { data } = inject(DatasetStore)) =>
        slices.reduce(
          (acc, slice) => ({
            ...acc,
            [slice]: computed(() => data.value()[slice]),
          }),
          { isLoading: data.isLoading },
        ) as Slices<T>,
    ),
  );
}
