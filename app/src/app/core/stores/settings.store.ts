/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { Course } from '@core/models/course';
import { Principal } from '@core/models/principal';
import { SessionStore } from '@core/stores/session.store';
import { sha256 } from '@core/utils/crypto.utils';
import { patchState, signalStore, withFeature, withMethods, withState } from '@ngrx/signals';
import { withSignalStorage } from './features/storage.feature';

/**
 * Genera la clave de almacenamiento con la función undireccional SHA256,
 * recortada en 16 caracteres para evitar grandes longitudes de clave en el storage.
 *
 * @param principal Usuario de sesión.
 * @returns Identificador único.
 */
function getKey(principal: Principal, course: Course): string {
  const token = `${principal.siteUrl}:${principal.id}:${course.id}`;
  const uniqueId = sha256(token).substring(0, 16);
  return `settings-${uniqueId}`;
}

/**
 * Propiedades de estado de la configuración.
 */
type SettingsState = {
  stores: Record<string, object>;
};

/**
 * Estado inicial.
 */
const initialState: SettingsState = {
  stores: {},
};

/**
 * Store de propiedades de estado de la configuración (ligada a sesión).
 */
export const SettingsStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store) => ({
    /**
     * Establece el estado de un store determinado.
     */
    setStoreState(key: string, state: object): void {
      patchState(store, ({ stores }) => ({ stores: { ...stores, [key]: state } }));
    },
  })),
  withFeature((_, { principal, course, currentPrincipal, currentCourse } = inject(SessionStore)) =>
    // La clave de almacenamiento es dinámica y ligada al usuario autenticado
    withSignalStorage(
      localStorage,
      computed(() => principal() && course() ? getKey(currentPrincipal(), currentCourse()) : null),
    ),
  ),
);
