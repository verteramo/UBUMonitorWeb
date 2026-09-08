import { computed } from '@angular/core';
import { withSettings } from '@core/stores/features/settings.feature';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';

/**
 * Propiedades de estado del dashboard.
 */
type DashboardState = {
  sidenav: boolean;
  panels: Record<string, boolean>;
};

/**
 * Estado inicial.
 */
const initialState: DashboardState = {
  sidenav: true,
  panels: { users: true },
};

export const DashboardStore = signalStore(
  withState(initialState),
  withComputed(({ panels }) => ({
    expanded: computed(() => (panel: string) => panels()[panel] || false),
  })),
  withMethods((store) => ({
    setSidenav(sidenav: boolean): void {
      patchState(store, { sidenav });
    },

    expand(panel: string, expanded: boolean): void {
      patchState(store, ({ panels }) => ({ panels: { ...panels, [panel]: expanded } }));
    },
  })),
  withSettings('dashboard'),
);
