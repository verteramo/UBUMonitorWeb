import { computed, inject, Signal } from '@angular/core';
import { signalStoreFeature, withProps } from '@ngrx/signals';
import { DatasetState, DatasetStore } from '../dataset.store';

type Slices<T extends keyof DatasetState> = {
  [K in T]: Signal<DatasetState[K]>;
} & {
  isLoading: Signal<boolean>;
};

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
