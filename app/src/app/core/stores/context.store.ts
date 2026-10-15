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
import { withRegistry } from './features/registry.feature';
import { withSignalStorage } from './features/storage.feature';

/**
 * Genera la clave de almacenamiento con la función unidireccional SHA256
 * recortada en 16 caracteres para evitar grandes longitudes de clave en el storage.
 *
 * @param user Usuario autenticado.
 * @param course Curso actual.
 * @returns Identificador único para el almacenamiento local.
 */
function getKey(user: User, course: Course, length?: number): string {
  const uniqueToken = `${user.siteUrl}:${user.id}:${course.id}`;
  const uniqueId = sha256(uniqueToken).substring(0, length);
  return `context-${uniqueId}`;
}

/**
 * Store de propiedades de estado de la configuración (ligada a sesión).
 */
export const ContextStore = signalStore(
  { providedIn: 'root' },
  withRegistry(),
  withFeature(() => {
    const { user, course } = inject(SessionStore);

    const key = computed(() => {
      const currentUser = user();
      const currentCourse = course();

      if (currentUser && currentCourse) {
        return getKey(currentUser, currentCourse);
      }

      return null;
    });

    return withSignalStorage(localStorage, key);
  }),
);
