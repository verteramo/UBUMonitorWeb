/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { ContextStore } from '@core/stores/context.store';
import { withRegistrySlot } from '@core/stores/features/registry.feature';
import { withPanels, withTabs } from '@core/stores/features/tab.feature';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

type DashboardState = {
  sidenavOpened: boolean;
};

const initialState: DashboardState = {
  sidenavOpened: true,
};

/**
 * Store local para el dashboard.
 */
export const DashboardStore = signalStore(
  withState(initialState),
  withTabs('visual', 'compare', 'forums', 'risk', 'enrolment', 'events', 'clustering'),
  withPanels({
    users: ['visual', 'compare', 'forums', 'risk', 'enrolment', 'clustering'],
    activities: ['visual', 'compare', 'forums', 'events', 'clustering'],
  }),
  withMethods((store) => ({
    /**
     * Establece el estado abierto de la barra lateral.
     *
     * @param sidenavOpened Nuevo estado.
     */
    setSidenavOpened(sidenavOpened: boolean): void {
      patchState(store, { sidenavOpened });
    },
  })),
  withRegistrySlot(ContextStore, 'dashboard'),
);
