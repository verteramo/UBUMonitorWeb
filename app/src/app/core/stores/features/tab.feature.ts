import { computed } from '@angular/core';
import {
  patchState,
  signalStoreFeature,
  withComputed,
  withMethods,
  withState,
} from '@ngrx/signals';

/**
 * Feature que añade la funcionalidad para administrar pestañas.
 *
 * @param tabs Array de pestañas.
 */
export function withTabs<T extends string>(tabs: readonly T[]) {
  return signalStoreFeature(
    withState<{ tab: T }>({ tab: tabs[0] }),
    withComputed(({ tab }) => ({
      tabIndex: computed(() => tabs.indexOf(tab())),
    })),
    withMethods((store) => ({
      setTabIndex(index: number): void {
        patchState(store, { tab: tabs[index] });
      },
    })),
  );
}
