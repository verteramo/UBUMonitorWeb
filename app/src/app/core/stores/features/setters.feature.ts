/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { toPascalCase } from '@core/utils/string.utils';
import { patchState, signalStoreFeature, withMethods } from '@ngrx/signals';

/**
 * Tipo que construye dinámicamente los métodos setters en formato PascalCase
 * a partir de un conjunto exacto de claves literales.
 */
type Setters<State, Keys extends keyof State> = {
  [K in Keys & string as `set${Capitalize<K>}`]: (value: State[K]) => void;
};

/**
 * Feature genérica para generar setters automáticos a partir de un array de claves tipadas.
 */
export function withSetters<State extends object, Keys extends keyof State>(
  keys: Keys[],
  onChange?: (key: Keys, updatedState: State & Record<string, any>, store: any) => void,
) {
  return signalStoreFeature(
    withMethods((store) => {
      const settersMap = keys.map((key) => [
        `set${toPascalCase(String(key))}`,
        (value: unknown): void => {
          patchState(store, (state: any) => {
            const updatedState = { ...state, [key]: value };

            if (onChange) {
              onChange(key, updatedState, store);
            }

            return updatedState;
          });
        },
      ]);

      return Object.fromEntries(settersMap) as Setters<State, Keys>;
    }),
  );
}
