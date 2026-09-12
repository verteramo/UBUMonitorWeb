/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed } from '@angular/core';
import { withSettingsSlice } from '@core/stores/features/settings-slice.feature';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';

/**
 * Paneles de la sidenav.
 */
type Panel = 'users' | 'activities';

/**
 * Pestañas del workspace.
 */
type Tab = 'visual' | 'compare' | 'forums' | 'risk' | 'enrollment' | 'events' | 'clustering';

/**
 * Propiedades de estado del dashboard.
 */
type DashboardState = {
  tab: Tab;
  sidenavOpened: boolean;
  panels: Partial<Record<Panel, boolean>>
};

/**
 * Estado inicial.
 */
const initialState: DashboardState = {
  tab: 'visual',
  sidenavOpened: true,
  panels: { users: true },
};

/**
 * Array de pestañas del workspace.
 */
const tabs: Tab[] = ['visual', 'compare', 'forums', 'risk', 'enrollment', 'events', 'clustering'];

/**
 * Configuración de visibilidad de los paneles.
 */
const panelsVisibility: Record<Panel, Tab[]> = {
  users: ['visual', 'compare', 'forums', 'risk', 'enrollment', 'clustering'],
  activities: ['visual', 'compare', 'forums', 'events', 'clustering'],
};

/**
 * Store local para el dashboard.
 */
export const DashboardStore = signalStore(
  withState(initialState),
  withComputed(({ tab }) => ({
    /**
     * Índice de la pestaña seleccionada.
     */
    tabIndex: computed(() => tabs.indexOf(tab())),
  })),
  withMethods(({ tab, panels }) => ({
    /**
     * Determina si un panel es visible.
     *
     * @param panel Panel.
     */
    isPanelVisible(panel: Panel): boolean {
      return panelsVisibility[panel].includes(tab());
    },

    /**
     * Determina si un panel está expandido.
     *
     * @param panel Panel.
     */
    isPanelExpanded(panel: Panel): boolean {
      return panels()[panel] || false;
    },
  })),
  withMethods((store) => ({
    /**
     * Establece la pestaña activa.
     *
     * @param tab Pestaña.
     */
    setTabIndex(index: number): void {
      patchState(store, { tab: tabs[index] });
    },

    /**
     * Establece el estado abierto de la barra lateral.
     *
     * @param sidenavOpened Nuevo estado.
     */
    setSidenavOpened(sidenavOpened: boolean): void {
      patchState(store, { sidenavOpened });
    },

    /**
     * Establece el estado expandido de un panel determinado.
     *
     * @param panel Panel.
     * @param expanded Nuevo estado.
     */
    setPanelExpanded(panel: Panel, expanded: boolean): void {
      patchState(store, ({ panels }) => ({ panels: { ...panels, [panel]: expanded } }));
    },
  })),
  withSettingsSlice('dashboard'),
);
