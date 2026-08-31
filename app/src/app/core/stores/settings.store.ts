/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
import { sha256 } from '@core/utils/crypto.utils';
import {
  patchState,
  signalStore,
  withComputed,
  withFeature,
  withMethods,
  withProps,
  withState,
} from '@ngrx/signals';
import { withSignalStorage } from './features/storage.feature';

/**
 * Genera la clave de almacenamiento con la función undireccional SHA256,
 * recortada en 16 caracteres para evitar grandes longitudes de clave en el storage.
 *
 * @param principal Usuario de sesión.
 * @returns Identificador único.
 */
function getKey({ id, platform: { url } }: Principal): string {
  const uniqueId = sha256(`${id}:${url}`).substring(0, 16);
  return `settings-state-${uniqueId}`;
}

/** Propiedades de estado del dasboard. */
type DashboardState = {
  /** Estado de apertura de la barra lateral. */
  sidenavOpened: boolean;

  /** Estado de expansión de los paneles del acordeón. */
  panels: Record<string, boolean>;
};

/** Estado inicial del dashboard. */
const initialDashboardState: DashboardState = {
  sidenavOpened: true,
  panels: { users: true },
};

/** Propiedades de estado de los criterios de tiempo. */
export type AccessCriterion = {
  days: number;
  color: string;
};

/** Estado inicial de los criterios de tiempo. */
export const initialTimeCriteriaState: AccessCriterion[] = [
  { days: 2, color: 'green' },
  { days: 6, color: 'blue' },
  { days: 12, color: 'yellow' },
];

/** Propiedades de estado de la configuración conjunta. */
type SettingsState = {
  dashboard: DashboardState;
  timeCriteria: AccessCriterion[];
};

/** Estado inicial. */
const initialState: SettingsState = {
  dashboard: initialDashboardState,
  timeCriteria: initialTimeCriteriaState,
};

/** Store de propiedades de estado de la configuración (ligada a sesión). */
export const SettingsStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withProps(() => ({
    _sessionStore: inject(SessionStore),
  })),
  withFeature(({ _sessionStore: { principal, currentPrincipal } }) =>
    // La clave de almacenamiento es dinámica y ligada al usuario autenticado
    withSignalStorage(
      localStorage,
      computed(() => (principal() ? getKey(currentPrincipal()) : null)),
    ),
  ),
  withComputed(({ dashboard }) => ({
    isPanelExpanded: computed(() => (panel: string) => {
      return dashboard.panels()[panel] || false;
    }),
  })),
  withMethods((store) => ({
    /** Establece el estado de apertura de la barra lateral. */
    setSidenavOpened(sidenavOpened: boolean) {
      patchState(store, ({ dashboard }) => ({
        dashboard: { ...dashboard, sidenavOpened },
      }));
    },

    /** Establece el estado de expansión de los paneles del acordeón. */
    setPanelExpanded(panel: string, expanded: boolean) {
      patchState(store, ({ dashboard }) => ({
        dashboard: {
          ...dashboard,
          panels: { ...dashboard.panels, [panel]: expanded },
        },
      }));
    },

    /** Añade un criterio de tiempo */
    addTimeCriterion(criterion: AccessCriterion) {
      patchState(store, ({ timeCriteria }) => ({
        timeCriteria: [...timeCriteria, criterion],
      }));
    },

    /** Elimina un criterio de tiempo */
    removeTimeCriterion(criterion: AccessCriterion) {
      patchState(store, ({ timeCriteria }) => ({
        timeCriteria: [...timeCriteria.filter((current) => current !== criterion)],
      }));
    },

    setTimeCriteria(timeCriteria: AccessCriterion[]) {
      patchState(store, { timeCriteria });
    },
  })),
);
