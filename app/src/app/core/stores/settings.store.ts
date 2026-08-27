/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
import { sha256 } from '@core/utils/crypto.utils';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

/**
 * Genera la clave de almacenamiento con el la función undireccional SHA256,
 * recortado en 16 caracteres para evitar grandes longitudes de clave en el storage.
 *
 * @param principal Usuario de sesión.
 * @returns Identificador único.
 */
function getStorageKey({ id, platform: { url } }: Principal): string {
  return sha256(`${id}:${url}`).substring(0, 16);
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

export

/** Estado inicial de los criterios de tiempo. */
const initialTimeCriteriaState: AccessCriterion[] = [
  { days: 2, color: 'green' },
  { days: 6, color: 'blue' },
  { days: 12, color: 'yellow' },
];

/** Propiedades de estado de la configuración conjunta. */
type SettingsState = {
  timeCriteria: AccessCriterion[];
  dashboard: DashboardState;
};

/** Estado inicial. */
const initialState: SettingsState = {
  timeCriteria: initialTimeCriteriaState,
  dashboard: initialDashboardState,
};

/** Store de propiedades de estado de la configuración (ligada a sesión). */
export const SettingsStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  // La clave de almacenamiento es dinámica y ligada al usuario autenticado
  withStorage(localStorage, () => {
    const session = inject(SessionStore);

    return computed(() => {
      const principal = session.principal();

      if (principal) {
        return `settings-state-${getStorageKey(principal)}`;
      }

      return null;
    });
  }),
  withComputed((store) => ({
    isPanelExpanded: computed(() => (panel: string) => {
      return store.dashboard.panels()[panel] || false;
    }),
  })),
  withMethods((store) => ({
    /** Establece el estado de apertura de la barra lateral. */
    setSidenavOpened(sidenavOpened: boolean) {
      patchState(store, (state) => ({
        dashboard: {
          ...state.dashboard,
          sidenavOpened,
        },
      }));
    },

    /** Establece el estado de expansión de los paneles del acordeón. */
    setPanelExpanded(panel: string, expanded: boolean) {
      patchState(store, (state) => ({
        dashboard: {
          ...state.dashboard,
          panels: {
            ...state.dashboard.panels,
            [panel]: expanded,
          },
        },
      }));
    },

    /** Añade un criterio de tiempo */
    addTimeCriterion(timeCriterion: AccessCriterion) {
      patchState(store, (state) => ({
        timeCriteria: [...state.timeCriteria, timeCriterion],
      }));
    },

    /** Elimina un criterio de tiempo */
    removeTimeCriterion(timeCriterion: AccessCriterion) {
      patchState(store, (state) => ({
        timeCriteria: [
          ...state.timeCriteria.filter((current) => {
            current == timeCriterion;
          }),
        ],
      }));
    },

    setTimeCriteria(timeCriteria: AccessCriterion[]) {
      patchState(store, { timeCriteria });
    },
  })),
);
