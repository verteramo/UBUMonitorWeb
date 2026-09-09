import { computed } from '@angular/core';
import { withSettingsSlice } from '@core/stores/features/settings-slice.feature';
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

/**
 * Store local para el dashboard.
 */
export const DashboardStore = signalStore(
  withState(initialState),
  withComputed(({ panels }) => ({
    /**
     * Determina si un panel está expandido.
     */
    expanded: computed(() => (panel: string) => panels()[panel] || false),
  })),
  withMethods((store) => ({
    /**
     * Establece el estado abierto de la barra lateral.
     *
     * @param sidenav Nuevo estado.
     */
    setSidenav(sidenav: boolean): void {
      patchState(store, { sidenav });
    },

    /**
     * Establece el estado expandido de un panel determinado.
     *
     * @param panel Panel.
     * @param expanded Nuevo estado.
     */
    expand(panel: string, expanded: boolean): void {
      patchState(store, ({ panels }) => ({ panels: { ...panels, [panel]: expanded } }));
    },
  })),
  withSettingsSlice('dashboard'),
);
