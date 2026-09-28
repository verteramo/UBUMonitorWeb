/*
 * Este fichero forma parte de UBUMonitorWeb.
 *
 * @author Marcelo Verteramo Pérsico
 */

import { computed, inject } from '@angular/core';
import { Course } from '@core/models/course';
import { User } from '@core/models/user';
import { SessionStore } from '@core/stores/session.store';
import { sha256 } from '@core/utils/crypto.utils';
import { signalStore, withFeature } from '@ngrx/signals';
import { withSlices } from './features/slices.feature';
import { withSignalStorage } from './features/storage.feature';

/**
 * Genera la clave de almacenamiento con la función undireccional SHA256,
 * recortada en 16 caracteres para evitar grandes longitudes de clave en el storage.
 *
 * @param principal Usuario de sesión.
 * @returns Identificador único.
 */
function getKey(principal: User, course: Course): string {
  const uniqueToken = `${principal.siteUrl}:${principal.id}:${course.id}`;
  const uniqueId = sha256(uniqueToken).substring(0, 16);
  return `settings-${uniqueId}`;
}

/**
 * Store de propiedades de estado de la configuración (ligada a sesión).
 */
export const SessionSettingsStore = signalStore(
  { providedIn: 'root' },
  withSlices(),
  withFeature((_, { principal, course, currentPrincipal, currentCourse } = inject(SessionStore)) =>
    // La clave de almacenamiento es dinámica y ligada al usuario autenticado
    withSignalStorage(
      localStorage,
      computed(() =>
        principal() && course() ? getKey(currentPrincipal(), currentCourse()) : null,
      ),
    ),
  ),
);
