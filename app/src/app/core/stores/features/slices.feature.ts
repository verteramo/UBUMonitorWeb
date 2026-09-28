import { inject, ProviderToken } from '@angular/core';
import {
  getState,
  patchState,
  signalStoreFeature,
  SignalStoreFeatureType,
  StateSignals,
  StateSource,
  watchState,
  withHooks,
  withMethods,
  withState,
} from '@ngrx/signals';

/*
 * Inferencia de instancias de signalStore que implementan una feature.
 *
 * https://github.com/ngrx/platform/discussions/4886#discussioncomment-13882114
 */

/**
 * Tipo que infiere únicamente las props públicas.
 */
type WithoutPrivates<Store extends object> = {
  [K in keyof Store as K extends `_${string}` ? never : K]: Store[K];
};

/**
 * Tipo que infiere las props de la instancia de una feature.
 */
export type FeatureInstanceType<Feature extends (...args: any) => any> = WithoutPrivates<
  SignalStoreFeatureType<Feature>['methods'] &
    SignalStoreFeatureType<Feature>['props'] &
    StateSignals<SignalStoreFeatureType<Feature>['state']>
> &
  StateSource<SignalStoreFeatureType<Feature>['state']>;

/**
 * Estado de los slices.
 */
type SlicesState = {
  slices: Record<string, object>;
};

/**
 * Feature para stores que actúan como contenedoras de slices.
 */
export function withSlices() {
  return signalStoreFeature(
    withState<SlicesState>({ slices: {} }),
    withMethods((store) => ({
      setSliceState(slice: string, state: object): void {
        patchState(store, ({ slices }) => ({ slices: { ...slices, [slice]: state } }));
      },
    })),
  );
}

/**
 * Feature para reservar un slice en una store contenedora de slices
 */
export function withSlice(
  sourceToken: ProviderToken<FeatureInstanceType<typeof withSlices>>,
  slice: string,
) {
  return signalStoreFeature(
    withHooks({
      onInit(store) {
        const source = inject(sourceToken);
        const initialState = getState(source).slices[slice];

        if (initialState) {
          patchState(store, initialState);
        }

        watchState(store, (state) => source.setSliceState(slice, state));
      },
    }),
  );
}
