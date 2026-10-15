import { inject, ProviderToken } from '@angular/core';
import {
  getState,
  patchState,
  SignalStoreFeature,
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
 * Inferencia de instancias de signalStoreFeature.
 *
 * https://github.com/ngrx/platform/discussions/4886#discussioncomment-13882114
 */
export type FeatureInstanceType<F extends () => SignalStoreFeature<any, any>> =
  SignalStoreFeatureType<F>['methods'] &
    SignalStoreFeatureType<F>['props'] &
    StateSignals<SignalStoreFeatureType<F>['state']> &
    StateSource<SignalStoreFeatureType<F>['state']>;

/**
 * Feature para stores que actúan como registros.
 *
 * @returns Feature del store con el estado slots y el método para actualizar sus subestados.
 */
export function withRegistry() {
  return signalStoreFeature(
    withState<{ slots: Record<string, object> }>({ slots: {} }),
    withMethods((store) => ({
      /**
       * Actualiza el estado de un slot específico dentro del registro.
       *
       * @param slot Clave identificativa del slot.
       * @param state Nuevo objeto de estado para el slot.
       */
      setState(slot: string, state: object): void {
        patchState(store, ({ slots }) => ({ slots: { ...slots, [slot]: state } }));
      },
    })),
  );
}

/**
 * Feature para reservar y sincronizar un slot dentro de un store registro.
 *
 * @param registryProvider Token de inyección del store registro.
 * @param slot Clave del slot que se va a reservar y sincronizar.
 * @returns Feature del store con la lógica de inicialización y sincronización bidireccional del slot.
 */
export function withRegistrySlot(
  registryProvider: ProviderToken<FeatureInstanceType<typeof withRegistry>>,
  slot: string,
) {
  return signalStoreFeature(
    withHooks((store) => ({
      onInit(): void {
        const registry = inject(registryProvider);
        const initialSlotState = getState(registry).slots[slot];

        /*
         * Si hay un estado inicial para este
         * slot en el registro, se hidrata el slot
         */
        if (initialSlotState) {
          patchState(store, initialSlotState);
        }

        /*
         * Se sincronizan continuamente
         * los cambios de este store en su slot
         */
        watchState(store, (state) => registry.setState(slot, state));
      },
    })),
  );
}
