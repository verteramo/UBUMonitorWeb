import { toPascalCase } from '@core/utils/string.utils';
import { patchState, signalStoreFeature, withMethods, withState } from '@ngrx/signals';

/**
 * Tipo para la inferencia de los setters de los filtros.
 */
type Setters<T> = {
  [K in keyof T & string as `set${Capitalize<K>}`]: (value: T[K]) => void;
};

export function withSetters<T extends object>(state: T) {
  return signalStoreFeature(
    withState(state),
    withMethods(
      (store) =>
        /**
         * Construcción de los setters de los filtros.
         */
        Object.fromEntries(
          Object.keys(state).map((key) => [
            `set${toPascalCase(key)}`,
            (value: unknown): void => patchState(store, { [key]: value } as any),
          ]),
        ) as Setters<T>,
    ),
  );
}
