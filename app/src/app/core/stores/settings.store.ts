/**
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
import { sha256 } from '@core/utils/crypto.utils';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { withStorage } from './features/storage.feature';

/**
 * Genera la clave de almacenamiento con el algoritmo asimétrico SHA256,
 * recortado en 16 caracteres para evitar grandes longitudes de clave en el storage.
 *
 * @param principal Usuario de sesión.
 * @returns Identificador único.
 */
function getStorageKey({ id, platform: { url } }: Principal): string {
  return sha256(`${id}:${url}`).substring(0, 16);
}

/** Propiedades de estado de la configuración. */
type SettingsState = {
  /** Estado de apertura de la barra lateral. */
  sidenavOpened: boolean;

  /** Estado de expansión de los paneles del acordeón. */
  _panels: Record<string, boolean>;
};

/** Estado inicial. */
const initialState: SettingsState = {
  sidenavOpened: true,
  _panels: {
    users: true,
  },
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
  withMethods((store) => ({
    /** Establece el estado de apertura de la barra lateral. */
    setSidenavOpened(opened: boolean) {
      patchState(store, { sidenavOpened: opened });
    },

    /** Establece el estado de expansión de los paneles del acordeón. */
    setPanelExpanded(panel: string, expanded: boolean) {
      patchState(store, (state) => ({
        _panels: {
          ...state._panels,
          [panel]: expanded,
        },
      }));
    },

    /** Obtiene el estado de expansión de los paneles del acordeón. */
    isPanelExpanded(panel: string) {
      return store._panels()[panel] || false;
    },

    /** Reinicializa la configuración completa a los valores por defecto. */
    clear() {
      patchState(store, initialState);
    },
  })),
);
